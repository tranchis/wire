package net.sf.ictalive.metamodel.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.gmf.runtime.notation.NotationPackage;

public class Serialiser<T extends EObject>
{
	private boolean					bCrossRef;
	private XMIResourceFactoryImpl	resourceFactory;
	private ResourceSet				resourceSet;

	public Serialiser(Class<?> factory)
	{
		this.bCrossRef = true;
		initialise(factory, "xmi");
	}

	public Serialiser(Class<?> factory, String extension)
	{
		this.bCrossRef = true;
		initialise(factory, extension);
	}
	
	public Serialiser(Class<?> factory, String extension, boolean bCrossRef)
	{
		this.bCrossRef = bCrossRef;
		initialise(factory, extension);
	}
	
	private void initialise(Class<?> factory, String extension)
	{
		resourceFactory = new XMIResourceFactoryImpl();
		
		resourceSet = new ResourceSetImpl(); 
//		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(extension, new XMIResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
				Resource.Factory.Registry.DEFAULT_EXTENSION, resourceFactory);

		try
		{
			resourceSet.getPackageRegistry().put((String)factory.getDeclaredField("eNS_PREFIX").get(factory), factory.getDeclaredField("eINSTANCE").get(factory));
			resourceSet.getPackageRegistry().put((String)factory.getDeclaredField("eNS_URI").get(factory), factory.getDeclaredField("eINSTANCE").get(factory));
			resourceSet.getPackageRegistry().put(NotationPackage.eNS_PREFIX, NotationPackage.eINSTANCE);
			resourceSet.getPackageRegistry().put(NotationPackage.eNS_URI, NotationPackage.eINSTANCE);
//			traverse((EObject)t);
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchFieldException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Resource deserialiseToResource(File file)
	{
		T					t;
		ResourceSet			rs;
		Resource			r;
		
		rs = getResourceSet();
		r = getResource(rs, URI.createFileURI(file.getAbsolutePath()), true);
		t = (T)r.getContents().get(0);
		traverse((EObject)t, new HashSet<EObject>());
		
		return r;
	}

	@SuppressWarnings("unchecked")
	public synchronized T deserialise(URI uri, boolean free)
	{
		T					t;
		ResourceSet			rs;
		Resource			r;
		
		rs = getResourceSet();
		r = getResource(rs, uri, true);
		t = (T)r.getContents().get(0);
//		traverse((EObject)t, new HashSet<EObject>());
//		EcoreUtil.resolveAll(t);
		if(free)
		{
//			r.unload();
			try
				{
				rs.getResources().clear();
				}
			catch (Exception e)
				{
				// TODO Auto-generated catch block
				System.err.println("DEBUG - problem desrialising (line 119)");
				System.err.println(e.getMessage());
						
				}
		}
		
		return t;
	}

	public T deserialise(URL url)
	{
		T	t;
		
		t = deserialise(URI.createURI(url.toString()), false);
		
		return t;
	}
	
	public synchronized T deserialise(File f) throws IOException
	{
		T	t;
		
		t = deserialise(URI.createFileURI(f.getAbsolutePath()), false);
		
		return t;
	}

	public T deserialiseAndFree(URL url)
	{
		T	t;
		
		t = deserialise(URI.createURI(url.toString()), true);
		
		return t;
	}
	
	public synchronized T deserialiseAndFree(File f) throws IOException
	{
		T	t;
		
		t = deserialise(URI.createFileURI(f.getAbsolutePath()), true);
		
		return t;
	}

	public T deserialise(String st) throws IOException
	{
		File				f;
		FileOutputStream	fos;
		T					t;
		
		try
		{
			f = File.createTempFile("event", ".xml");
			fos = new FileOutputStream(f);
			fos.write(st.getBytes());
			fos.close();

			t = deserialise(f); // TODO: Bypass the need for a temp file
			
			f.delete();
			
			return t;
		}
		catch (IOException e)
		{
			throw e;
		}
	}

	public T deserialiseAndFree(String st) throws IOException
	{
		File				f;
		FileOutputStream	fos;
		T					t;
		
		try
		{
//		System.err.println(st);
			f = File.createTempFile("event", ".xml");
			fos = new FileOutputStream(f);
			fos.write(st.getBytes());
			fos.close();

			t = deserialiseAndFree(f); // TODO: Bypass the need for a temp file
			
			f.delete();
			
			return t;
		}
		catch (NullPointerException e)
		{
			System.err.println("DEBUG - problem deserialing and Free");
//			System.err.println(e.getMessage());
			throw e;
		}
		catch (IOException e)
		{
			throw e;
		}
	}

	int addCounter = 0;
	private void addToResource(EObject e, Resource resource, boolean bAdd)
	{
		addCounter++;
		
		EList<EObject>		el;
		Iterator<EObject>	it;
		EObject				recurse;
		
		if(bAdd) // && !resource.getContents().contains(e))
		{
//			System.out.println("Adding " + e);
			resource.getContents().add(e);
		}
		
//		System.out.println("Exploring " + e);
		
		it = e.eContents().iterator();
		try
		{
			while(it.hasNext())
			{
				recurse = it.next();
				if(!resource.getContents().contains(recurse))
				{
					addToResource(recurse, resource, false);
				}
			}
		}
		catch(Exception ex)
		{
			// TODO: Fix this (?)
		System.err.println("DUBUG - problem adding to Resource");
		System.err.println(ex.getMessage());
		}
		
		if(bCrossRef)
		{
			el = e.eCrossReferences();
			it = el.iterator();
			try
			{
				while(it.hasNext())
				{
					recurse = it.next();
//					if(!resource.getContents().contains(recurse))
					if((!resource.getContents().contains(recurse)) && (!resource.equals(recurse.eResource())))
					{
						addToResource(recurse, resource, true);
					}
				}
			}
			catch(NullPointerException npe)
			{
				// TODO: Fix this (?)
				System.err.println("Crashed at " + el);
				if(addCounter > 10)
					{
					System.err.println("tried adding to resource too many time.  give up");
					return;
					}
				addToResource(e, resource, bAdd);
			}
		}
	}
	
	public String serialise(T t) throws IOException
	{
		Resource				resource;
		File					f;
		FileInputStream			fis;
		byte[]					by;
		String					res = null;
		ResourceSet				rs;

		f = File.createTempFile("event", ".xml");

		rs = getResourceSet();
//		resource = t.eResource();
//		resource.setURI(URI.createFileURI(f.getAbsolutePath()));
		resource = getResource(rs, URI.createFileURI(f.getAbsolutePath()));
		addCounter = 0;
		addToResource((EObject)t, resource, true);
//		System.out.println(resource.getContents());
		
		try
		{
			resource.save(null);
			fis = new FileInputStream(f);
			by = new byte[fis.available()];
			fis.read(by);

			res = new String(by);
//			System.err.println(res);
		}
		catch (IOException e)
		{
			throw e;
		}
		catch(Exception e1)
		{
		System.err.println("DEBUG - Error in serialising the event, sending again");
		serialise(t);

		}

		try
			{
			resource.getContents().clear();
			resource.unload();
			}
		catch (Exception e)
			{
			System.err.println("DEBUG - Error in unloading the resource");
			System.err.println(e.getMessage());
			}
//		rs.getResources().clear();

		return res;
	}

	private Resource getResource(ResourceSet rs, URI uri)
	{
		Resource	resource;
		
		resource = rs.createResource(uri);
		
		return resource;
	}

	private ResourceSet getResourceSet()
	{
		return resourceSet;
	}

	private Resource getResource(ResourceSet rs, URI uri, boolean mode)
	{
		Resource	resource;
		
		resource = rs.getResource(uri, mode);
		
		return resource;
	}

	private void traverse(EObject e, HashSet<EObject> objs)
	{
		EList<EObject>		el;
		Iterator<EObject>	it;
		EObject				recurse;
		
		if(!objs.contains(e))
		{
			objs.add(e);
	
			el = e.eCrossReferences();
			it = el.iterator();
			while(it.hasNext())
			{
				recurse = it.next();
				traverse(recurse, objs);
			}
			
			it = e.eContents().iterator();
			while(it.hasNext())
			{
				recurse = it.next();
				traverse(recurse, objs);
			}
		}
	}
}
