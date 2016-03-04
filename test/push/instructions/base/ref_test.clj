(ns push.instructions.base.ref_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:require [push.util.stack-manipulation :as s])
  (:require [push.types.core :as t])
  (:use [push.types.base.ref])
  )


(fact "ref-type knows some instructions"
  (keys (:instructions ref-type)) =>
    (contains [:ref-equal? :ref-flush :ref->code :ref-stackdepth] :in-any-order :gaps-ok))




(tabular
  (fact ":ref-new creates a new `:ref!\\d\\d\\d` keyword"
    (register-type-and-check-instruction
        ?set-stack ?items ref-type ?instruction ?get-stack) =>
            ?expected)

    ?set-stack  ?items      ?instruction  ?get-stack   ?expected
    :ref        '()         :ref-new       :ref        #(keyword? (first %))
    :ref        '()         :ref-new       :ref        #(re-seq #":ref\!\d+" (str (first %))))


(fact ":push-quoterefs turns on the interpreter's :quote-refs? flag"
  (let [no (push.interpreter.templates.one-with-everything/make-everything-interpreter)]
    (:quote-refs? no) => nil
    (:quote-refs? (i/execute-instruction no :push-quoterefs)) => true))



(fact ":push-unquoterefs turns off the interpreter's :quote-refs? flag"
  (let [no 
    (assoc (push.interpreter.templates.one-with-everything/make-everything-interpreter)
      :quote-refs? true)]
    (:quote-refs? no) => true
    (:quote-refs? (i/execute-instruction no :push-unquoterefs)) => false
    ))



(fact ":ref-clear completely clears a binding"
  (let [hasref 
    (assoc
      (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:ref '(:x)})
      :bindings {:x '(1 2 (3 4))})]

    (push.core/get-stack hasref :ref) => '(:x)

    (:bindings (i/execute-instruction hasref :ref-clear)) => {:x '()}
    (:bindings (i/execute-instruction
      (s/set-stack hasref :ref '(:bad)) :ref-clear)) => '{:x (1 2 (3 4))}
    ))



(fact ":ref-fullquote copies the entire :ref binding stack onto the :code stack, w/o discarding it"
  (let [hasref 
    (assoc
      (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:ref '(:x)})
      :bindings {:x '(1 2 (3 4))})]
    (push.core/get-stack hasref :ref) => '(:x)
    (push.core/get-stack hasref :code) => '()
    (:bindings hasref) => {:x '(1 2 (3 4))}


    (:bindings (i/execute-instruction hasref :ref-fullquote)) => {:x '(1 2 (3 4))}
    (push.core/get-stack (i/execute-instruction hasref :ref-fullquote) :code) => '((1 2 (3 4)))
    (push.core/get-stack
      (i/execute-instruction (i/push-item hasref :ref :f) :ref-fullquote) :code) => '(())
    ))



(fact ":ref-dump copies the entire :ref binding stack onto the :exec stack, w/o discarding it"
  (let [hasref 
    (assoc
      (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:ref '(:x)})
      :bindings {:x '(1 2 (3 4))})]
    (push.core/get-stack hasref :ref) => '(:x)
    (push.core/get-stack hasref :exec) => '()
    (:bindings hasref) => {:x '(1 2 (3 4))}


    (:bindings (i/execute-instruction hasref :ref-dump)) => {:x '(1 2 (3 4))}
    (push.core/get-stack (i/execute-instruction hasref :ref-dump) :exec) => '((1 2 (3 4)))
    (push.core/get-stack
      (i/execute-instruction (i/push-item hasref :ref :f) :ref-dump) :exec) => '(())
    ))


(fact ":ref-forget completely forgets a binding"
  (let [hasref 
    (assoc
      (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:ref '(:x)})
      :bindings {:x '(1 2 (3 4))})]

    (push.core/get-stack hasref :ref) => '(:x)

    (:bindings (i/execute-instruction hasref :ref-forget)) => {}
    (:bindings (i/execute-instruction
      (s/set-stack hasref :ref '(:bad)) :ref-forget)) => '{:x (1 2 (3 4))}
    ))



(fact ":ref-lookup pushes the bound :ref item to :exec"
  (let [hasref 
    (assoc
      (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:ref '(:x)})
      :bindings {:x '(1 2 (3 4))})]
    (push.core/get-stack hasref :ref) => '(:x)
    (push.core/get-stack hasref :exec) => '()
    (:bindings hasref) => {:x '(1 2 (3 4))}


    (:bindings (i/execute-instruction hasref :ref-lookup)) => {:x '(1 2 (3 4))}
    (push.core/get-stack (i/execute-instruction hasref :ref-lookup) :exec) => '(1)
    (push.core/get-stack
      (i/execute-instruction (i/push-item hasref :ref :f) :ref-lookup) :exec) => '()
    ))
