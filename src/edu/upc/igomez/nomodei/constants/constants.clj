;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Copyright (c) 2015 Ignasi Gómez Sebastià
; 
; All rights reserved. This program and the accompanying materials
; are made available under the terms of the Eclipse Public License v1.0
; which accompanies this distribution, and is available at
; http://www.eclipse.org/legal/epl-v10.html
; 
; Contributors:
;     Ignasi Gómez-Sebastià - Wrapper to mongoDB (2015-07-17) (yyyy-mm-dd)
;                             
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;(load-file "/Users/igomez/deapt/dea-repo/nomodei/src/edu/upc/igomez/nomodei/constants/constants.clj")
;(use 'edu.upc.igomez.nomodei.constants.constants)

(ns edu.upc.igomez.nomodei.constants.constants
 (:use [clojure.tools.logging :only (info error)])
  (:require [snippets-generic :as cs]
             [clojure.string :as str]))

(def props
  "Properties taken from `aggregator.properties`. These include:

  * Information (e.g., name, coordinates) about the city to crawl.
  * Access configuration to access the `mongo` instance."
  (cs/load-props "wire.properties"))

(def mode-demo
  (:mode.demo props))

(def test-sleep
  (:test.sleep props))
