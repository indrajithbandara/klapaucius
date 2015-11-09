(ns push.types.base.string
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [clojure.string :as strings])
  )


;; string-specific


(def string-concat
  (core/build-instruction
    string-concat
    :tags #{:string :base}
    (d/consume-top-of :string :as :arg2)
    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1 :arg2] #(str %1 %2) :as :both)
    (d/push-onto :string :both)))


(def string-length
  (core/build-instruction
    string-length
    :tags #{:string :base}
    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1] #(count %1) :as :len)
    (d/push-onto :integer :len)))


(def string-reverse
  (core/build-instruction
    string-reverse
    :tags #{:string :base}
    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1] #(strings/reverse %1) :as :sdrawkcab)
    (d/push-onto :string :sdrawkcab)))


(def classic-string-type
  ( ->  (t/make-type  :string
                      :recognizer string?
                      :attributes #{:string :base})
        t/make-visible 
        t/make-equatable
        t/make-comparable
        t/make-movable
        (t/attach-instruction , string-concat)
        (t/attach-instruction , string-length)
        (t/attach-instruction , string-reverse)
        ))
