(ns eu.superhub.wp4.monitor.core.LispToDrools
  (:require [clojure.pprint :as ppr])
  (:require [clojure.string :as stt])
  (:require [eu.superhub.wp4.monitor.core.regulative-parser :as regp]))

(import (eu.superhub.wp4.monitor.core.rules.drools DroolsEngine))
(import (eu.superhub.wp4.monitor.core.rules.drools.schema
          Rule Lhs Not Pattern FieldConstraint VariableRestriction LiteralRestriction
          ReturnValueRestriction FieldBinding Import))
(import (java.util LinkedList))

(defn find-first
  "Returns the first item of coll for which (pred item) returns logical true.
  Consumes sequences up to the first match, will consume the entire sequence
  and return nil if no match is found."
  [pred coll]
  (first (filter pred coll)))

(defn in? 
  "true if seq contains elm"
  [seq elm]  
  (some #(= elm %) seq))

(defn restriction-contains [v r]
  (and
    (instance? FieldConstraint r)
    (instance? VariableRestriction (. (. r getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction) get 0))
    (= (. (. (. r getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction) get 0) getIdentifier))))

(defn institution [id & n]
; 	p = new Package();
;		p.setName("net.sf.ictalive.monitoring.domain");
;		p.getImportOrImportfunctionOrGlobal().addAll(cr);
;		pn.setRules(p);
  (def pack (eu.superhub.wp4.monitor.core.rules.drools.schema.Package.))
  (def i (Import.))
  (. pack setName id)
  (. i setName "eu.superhub.wp4.monitor.core.domain.*")
  ;(println (class (to-array n)))
  (. (. pack getImportOrImportfunctionOrGlobal) add i)
  (. (. pack getImportOrImportfunctionOrGlobal) addAll (flatten n))
  pack)

(defn update-name [id r]
  (. r setName (str id "_" (. r getName)))
  r)

(defn update-order [idx r]
  (. r setName (str (. r getName) "_" idx))
  r)

(defn set-name [id r]
  (. r setName id)
  r)

(defn fill-info-countsas [r id]
;   p = new Pattern();
;		p.setObjectType("CountsAs");
;		fb = new FieldBinding();
;		fb.setFieldName(type);
;		fb.setIdentifier("f");
;		p.getFieldBindingOrFieldConstraintOrFrom().add(fb);
;		fc = new FieldConstraint();
;		fc.setFieldName("id");
;		lr = new LiteralRestriction();
;		lr.setEvaluator("==");
;		lr.setValue(code);
;		fc.getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction().add(lr);
;		p.getFieldBindingOrFieldConstraintOrFrom().add(fc);
;		lhs.getAbstractConditionalElementOrNotOrExists().add(p);
  (def lhs (. r getLhs))
  (def p (Pattern.))
  (def fb (FieldBinding.))
  (def fc (FieldConstraint.))
  (def lr (LiteralRestriction.))
  (. p setObjectType "CountsAs")
  (. fb setFieldName (nth (stt/split (. r getName) #"_") 1))
  (. fb setIdentifier "f")
  (. (. p getFieldBindingOrFieldConstraintOrFrom) add fb)
  (. fc setFieldName "id")
  (. lr setEvaluator "==")
  (. lr setValue id)
  (. (. fc getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction) add lr)
  (. (. p getFieldBindingOrFieldConstraintOrFrom) add fc)
  (. (. lhs getAbstractConditionalElementOrNotOrExists) add p)
  r)

(defn fill-info-rule [r id]
  (def lhs (. r getLhs))
;		p = new Pattern();
;		p.setIdentifier("rulenorm");
;		p.setObjectType("Norm");
;		fc = new FieldConstraint();
;		fc.setFieldName("normID");
;		lr = new LiteralRestriction();
;		lr.setEvaluator("==");
;		lr.setValue(code);
;		fc.getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction().add(lr);
;		p.getFieldBindingOrFieldConstraintOrFrom().add(fc);
;		lhs.getAbstractConditionalElementOrNotOrExists().add(p);
  (def p (Pattern.))
  (def fc (FieldConstraint.))
  (def lr (LiteralRestriction.))
  (. p setIdentifier "rulenorm")
  (. p setObjectType "Norm")
  (. fc setFieldName "normID")
  (. lr setEvaluator "==")
  (. lr setValue id)
  (. (. fc getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction) add lr)
  (. (. p getFieldBindingOrFieldConstraintOrFrom) add fc)
  (. (. lhs getAbstractConditionalElementOrNotOrExists) add p)
;		p = new Pattern();
;		p.setObjectType(type.substring(0, 1).toUpperCase() + type.substring(1));
;		fc = new FieldConstraint();
;		fc.setFieldName("norm");
;		vr = new VariableRestriction();
;		vr.setEvaluator("==");
;		vr.setIdentifier("rulenorm");
;		fc.getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction().add(vr);
;		p.getFieldBindingOrFieldConstraintOrFrom().add(fc);
  (def p2 (Pattern.))
  (def fc2 (FieldConstraint.))
  (def vr (VariableRestriction.))
  (def ty (stt/split (. r getName) #"_"))
  (def sty (stt/capitalize (nth ty (- (count ty) 2))))
  (. p2 setObjectType sty)
  (. fc2 setFieldName "norm")
  (. vr setEvaluator "==")
  (. vr setIdentifier "rulenorm")
  (. (. fc2 getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction) add vr)
  (. (. p2 getFieldBindingOrFieldConstraintOrFrom) add fc2)
;		fb = new FieldBinding();
;		fb.setFieldName("formula");
;		fb.setIdentifier("f");
;		p.getFieldBindingOrFieldConstraintOrFrom().add(fb);
;		lhs.getAbstractConditionalElementOrNotOrExists().add(p);
  (def fb (FieldBinding.))
  (. fb setFieldName "formula")
  (. fb setIdentifier "f")
  (. (. p2 getFieldBindingOrFieldConstraintOrFrom) add fb)
  (. (. lhs getAbstractConditionalElementOrNotOrExists) add p2)
  r)

(defn norm [id cs]
;		vr = new Vector<Rule>()
;		vr.addAll(cp.parseCondition(norm, ConditionHolder.ACTIVATION));
;		vr.addAll(cp.parseCondition(norm, ConditionHolder.MAINTENANCE));
;		vr.addAll(cp.parseCondition(norm, ConditionHolder.DEACTIVATION));
  (def m (map #(update-name id %) (flatten cs)))
  (map #(fill-info-rule % id) m))

(defn conditions [& cs]
  cs)

(defn get-rhs-value [v]
  (str "\t\tvalue = Value.createValue(\"" v "\", " v ");\n"
       "\t\tinsertLogical(value);\n"
       "\t\ttheta.add(value);\n"))

(defn add-rhs [r vars]
  (def rhs (str (apply str "\n\t\tSubstitution theta = new Substitution();\n"
    "\t\tValue value;\n"
    (map get-rhs-value vars))
                "\n\t\ttheta = Substitution.getSubstitution(theta);\n"
                "\t\tinsertLogical(theta);\n"
                "\t\tinsertLogical(new Holds(f, theta));\n"
                "\t\tSystem.out.println(\"" (. r getName) " fired with substitution: [\" + theta + \"]\");\n"))
  (. r setRhs rhs)
  r)

(defn condition [id ors]
  (def ors-with-rhs (map #(add-rhs (first %) (second %)) ors))
  (map-indexed update-order (map #(set-name id  %) ors-with-rhs)))

(defn disjunction [& ands]
  ands)

(defn is-variable-constraint? [field-something]
  (and
    (instance? FieldConstraint field-something)
    (instance? VariableRestriction (. (. field-something getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction) get 0))))

(defn get-variable-in-constraint [field-variable]
  (. (. (. field-variable getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction) get 0) getIdentifier))

(defn get-variables-within [atoms]
  ; atoms are Patterns
  (map #(map get-variable-in-constraint
         (filter is-variable-constraint? (. % getFieldBindingOrFieldConstraintOrFrom))) atoms))

(defmulti remove-not class)

(defmethod remove-not Pattern [p]
  p)

(defmethod remove-not Not [n]
  (. (. n getAbstractConditionalElementOrNotOrExists) get 0))

(defn filter-nots [atoms]
  (map remove-not atoms))

(defmulti pattern-contains (fn [a b] (class b)))

(defmethod pattern-contains Pattern [v p]
  (not (empty? (filter #(restriction-contains v %)
                (. p getFieldBindingOrFieldConstraintOrFrom)))))

(defmethod pattern-contains Not [v n]
  (pattern-contains v (. (. n getAbstractConditionalElementOrNotOrExists) get 0)))

(defmulti replace-in-pattern (fn [a b] (class b)))

(defn constraint-to-binding [v fc]
  "Input: FieldConstraint"
  "Output: FieldBinding"
;			fb = new FieldBinding();
;			fb.setFieldName(fc.getFieldName());
;			fb.setIdentifier(vr.getIdentifier()); // TODO: Pending fix
;			p.getFieldBindingOrFieldConstraintOrFrom().add(fb);
;			p.getFieldBindingOrFieldConstraintOrFrom().remove(fc);
;			context.add(vr.getIdentifier()); // TODO: Pending fix
  (def fb (FieldBinding.))
  (def vr (. (. fc getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction) get 0))
  (. fb setFieldName (. fc getFieldName))
  (. fb setIdentifier (. vr getIdentifier))
  fb)

(defmethod replace-in-pattern Pattern [v p]
  "Input: variable to convert into binding, and the target pattern"
  "Output: modified version of the pattern, with a binding in the first appearance of the input variable"
  (def rrs (. p getFieldBindingOrFieldConstraintOrFrom))
  (def fc (find-first #(restriction-contains v %) rrs))
  (def newrrs (replace {fc (constraint-to-binding v fc)} rrs))
  (dorun newrrs)
  (. (. p getFieldBindingOrFieldConstraintOrFrom) clear)
  (. (. p getFieldBindingOrFieldConstraintOrFrom) addAll newrrs)
  p)

(defmethod replace-in-pattern Not [v n]
  "Input: variable to convert into binding, and the target negative pattern (a Not)"
  "Output: a sequence with the original not, plus a pattern with the positive binding of the variable"
  "Example: ~is_in_house(john, 6pm) -> is_in_house(x, ?) and x = john and ~is_in_house(john, 6pm)"
  "Or, in other words: there exists an instance of the pattern p with a certain variable, that doesn't comply a certain Not statement"
  "This is a trick to overcome Drools limitations with expressivity, i.e. bindings can't be put into Not patterns"
  (def p (. (. n getAbstractConditionalElementOrNotOrExists) get 0))
  (def p-positive (Pattern.))
  (def rrs (. p getFieldBindingOrFieldConstraintOrFrom))
  (def fc-negative (find-first #(restriction-contains v %) rrs))
  (def fc (FieldConstraint.))
  (def vr (VariableRestriction.))
  (. (. p-positive getFieldBindingOrFieldConstraintOrFrom) add (. (. p getFieldBindingOrFieldConstraintOrFrom) get 0)) ; Copy p0: predicate name. TODO: Unsafe
  (. p-positive setObjectType "Proposition")
  (. vr setEvaluator "==")
  (. vr setIdentifier v)
  (. fc setFieldName (. fc-negative getFieldName))
  (. (. fc getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction) add vr)
  (. (. p-positive getFieldBindingOrFieldConstraintOrFrom) add fc)  
  (def p-positive-with-binding (replace-in-pattern v p-positive))
  (list p-positive-with-binding n))

(defn replace-constraint [v atoms]
  "Input: v is the variable to check, atoms is a set of patterns: {Pattern, Not}"
  "Output: a modified set of patterns, in which the first appearance of the variable is changed to binding"
  (def fp (find-first #(pattern-contains v %) atoms))
  (flatten (replace {fp (replace-in-pattern v fp)} atoms))) ; The flatten is due to the possibility of receiving a sequence (see replace-in-pattern(Not))

(defn conjunction [& atoms]
  (def l (Lhs.))
  (def r (Rule.))
  (def vars (distinct (flatten (get-variables-within (filter-nots atoms)))))
  (loop [m atoms v vars]
    (if (not (empty? v))
      (do
        (recur (replace-constraint (first v) m) (rest v)))
      (do
        (. (. l getAbstractConditionalElementOrNotOrExists) clear)
        (. (. l getAbstractConditionalElementOrNotOrExists) addAll m))))
  ;(. (. l getAbstractConditionalElementOrNotOrExists) addAll atoms)
  (. r setLhs l)
  (list r vars))

(defn constant [c]
;		content = ct.getName();
;		if(content.charAt(0) == '"')
;		{
;			content = content.substring(1, content.length() - 1);
;		}
;		fc = new FieldConstraint();
;		fc.setFieldName("p" + i);
;		lr = new LiteralRestriction();
;		lr.setEvaluator("==");
;		lr.setValue(content);
;		fc.getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction().add(lr);
  (def fc (FieldConstraint.))
  (def lr (LiteralRestriction.))
  (. lr setEvaluator "==")
  (. lr setValue c)
  (. (. fc getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction) add lr)
  fc)

(defn create-predicate-name [id]
  (constant id))

(defn argument-order [idx term]
  (. term setFieldName (str "p" idx))
  term)

(defn predicate [id & terms]
  (def p (Pattern.))
  (. p setObjectType "Proposition")
  (def t (cons (create-predicate-name id) (remove nil? (flatten terms))))
  (. (. p getFieldBindingOrFieldConstraintOrFrom) addAll (map-indexed argument-order t))
  p)

(defn arguments [& args]
  args)

(defn parameters [& params]
  params)

(defn variable [v]
;		name = v.getName();
;		fc = new FieldConstraint();
;		fc.setFieldName("p" + i);
;		vr = new VariableRestriction();
;		vr.setEvaluator("==");
;		vr.setIdentifier(name);
;		fc.getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction().add(vr);
;		res = fc;
  (def fc (FieldConstraint.))
  (def vr (VariableRestriction.))
  (. vr setEvaluator "==")
  (. vr setIdentifier v)
  (. (. fc getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction) add vr)
  fc)

(defn negation [at]
  (def n (Not.))
  (. (. n getAbstractConditionalElementOrNotOrExists) add at)
  n)

(defn function [n ps]
  (def fc (FieldConstraint.))
  (def rr (ReturnValueRestriction.))
  (. rr setEvaluator "==")
  (if (in? (list "+" "<" "=" "==" ">" "!=" "**" "-" "*" "/") n)
    (do
      (. rr setValue (apply str (interpose n (map #(. (. (. % getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction) get 0) getValue) ps)))))
    (do
      (. rr setValue (str (apply str n "(" (interpose ", " (map #(. (. (. % getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction) get 0) getValue) ps))) ")"))))
  (. (. fc getLiteralRestrictionOrVariableRestrictionOrReturnValueRestriction) add rr)
  fc)

(defn analyze-rule [r]
  (map #(do (class %)) (. (. r getLhs) getAbstractConditionalElementOrNotOrExists)))

(defn context [nm]
  (condition "context" (disjunction (conjunction (predicate "Context" (arguments (constant nm)))))))

(defn concrete-fact [ors]
  (def ors-with-rhs (map #(add-rhs (first %) (second %)) ors))
  (map-indexed update-order (map #(set-name "gamma1" %) ors-with-rhs)))

(defn abstract-fact [ors]
  nil)

(defn counts-as [id ct cf af]
;		f = new Formula((PartialStateDescription) ca.getConcreteFact());
;		fc = new Formula(a);
;		cas = new net.sf.ictalive.monitoring.domain.CountsAs(ca.hashCode() + "", f, new Formula(ca.getAbstractFact()), fc);
;		countsas.add(cas);
;		vr = new Vector<Rule>();
;		vr.addAll(cp.parseCondition(cas, ConditionHolder.GAMMA1));
;		vr.addAll(cp.parseCondition(cas, ConditionHolder.CONTEXT));
  (def m (map #(update-name id %) (flatten (list cf ct))))
  (map #(fill-info-countsas % id) m))

(defn load-opera [st]
  (def d (DroolsEngine.))
  (. d addPackage (eval (regp/parse-file st)))
  d)

(defn frm-load 
  "Load a clojure form from file." 
  [#^java.io.File file] 
  (with-open [r (java.io.PushbackReader. 
     (java.io.FileReader. file))] 
     (let [rec (read r)] 
      rec)))

(defn ^Package lisp-to-drools [^String st]
  (eval (frm-load (java.io.File. st))))

(defn ^Package opera-to-drools [^String st]
  (eval (regp/parse-file st)))
