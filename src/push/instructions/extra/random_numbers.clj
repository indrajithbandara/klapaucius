(ns push.instructions.extra.random-numbers
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


(def integer-uniform
  (core/build-instruction
    integer-uniform
    "`:integer-uniform` pops the top `:integer` value, and pushes a uniform random integer sampled from the range [0,:int). If the integer is negative, a negative result is returned."
    :tags #{:numeric :random}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(cond
                          (neg? %1) (- (rand-int %1))
                          (zero? %1) 0 
                          :else (rand-int %1)) :as :result)
    (d/push-onto :integer :result)))


(def float-uniform
  (core/build-instruction
    float-uniform
    "`:float-uniform` pops the top `:float` value, and pushes a random float uniformly sampled from the range [0,:f). If the float is negative, a negative result is returned."
    :tags #{:numeric :random}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(* (rand) %1) :as :result)
    (d/push-onto :float :result)))



;;;;;;;;;;;;;;;;;


(def random-numbers-module
  ( ->  (t/make-module  :random-numbers
                        :attributes #{:numeric :random})

        (t/attach-instruction , integer-uniform)
        (t/attach-instruction , float-uniform)
        ))
