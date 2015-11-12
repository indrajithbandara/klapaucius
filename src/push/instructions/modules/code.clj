(ns push.instructions.modules.code
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [push.util.code-wrangling :as u])
  )


(defn push-code? [item] (and (list? item) (= (first item) 'quote)))


;; code-specific instructions

; code_discrepancy
; code_extract
; code_insert
; code_map
; code_nth
; code_nthcdr
; code_overlap
; code_position
; code_subst


(def code-append
  (core/build-instruction
    code-append
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1] #(if (coll? %1) %1 (list %1)) :as :list1)
    (d/calculate [:arg2] #(if (coll? %1) %1 (list %1)) :as :list2)
    (d/calculate [:list1 :list2] #(concat %1 %2) :as :both)
    (d/push-onto :code :both)))



(def code-atom? (t/simple-1-in-predicate :code "atom?" #(not (coll? %1))))


(def code-cons (t/simple-2-in-1-out-instruction 
                    :code 
                    "cons" #(if (seq? %2) 
                                (conj %2 %1) 
                                (conj (list %2) %1))))



(def code-container (t/simple-2-in-1-out-instruction
                      :code 
                      "container" #(first (u/containers-in %1 %2))))


(def code-contains?
  (core/build-instruction
    code-contains?
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1 :arg2] #(u/contains-anywhere? %1 %2) :as :found)
    (d/push-onto :boolean :found)))


(def code-do
  (core/build-instruction
    code-do
    :tags #{:complex :base}
    (d/save-top-of :code :as :do-this)
    (d/calculate [:do-this] #(list %1 :code-pop) :as :continuation)
    (d/push-onto :exec :continuation)))


(def code-do*
  (core/build-instruction
    code-do*
    :tags #{:complex :base}
    (d/consume-top-of :code :as :do-this)
    (d/push-onto :exec :do-this)))


(def code-do*count
  (core/build-instruction
    code-do*count
    :tags #{:complex :base}
    (d/consume-top-of :code :as :do-this)
    (d/consume-top-of :integer :as :counter)
    (d/calculate [:counter] #(pos? %1) :as :go?)
    (d/calculate
      [:do-this :counter :go?] 
      #(if %3 
        (list %2 0 :code-quote %1 :code-do*range) 
        (list %2 :code-quote %1)) :as :continuation)
    (d/push-onto :exec :continuation)))


(def code-do*range
  (core/build-instruction
    code-do*range
    :tags #{:complex :base}
    (d/consume-top-of :code :as :do-this)
    (d/consume-top-of :integer :as :end)
    (d/consume-top-of :integer :as :start)
    (d/calculate [:start :end] #(= %1 %2) :as :done?)
    (d/calculate [:start :end] #(+ %1 (compare %2 %1)) :as :next)
    (d/calculate
      [:do-this :start :end :next :done?] 
      #(if %5
           (list %3 %1)
           (list %2 %1 (list %4 %3 :code-quote %1 :code-do*range))) :as :continuation)
    (d/push-onto :exec :continuation)))


(def code-do*times
  (core/build-instruction
    code-do*times
    :tags #{:complex :base}
    (d/consume-top-of :code :as :do-this)
    (d/consume-top-of :integer :as :count)
    (d/calculate [:count] #(zero? %1) :as :done?)
    (d/calculate [:count] #(+ %1 (compare 0 %1)) :as :next)
    (d/calculate
      [:do-this :count :next :done?] 
      #(if %4
           %1
           (list %1 (list %3 :code-quote %1 :code-do*times))) :as :continuation)
    (d/push-onto :exec :continuation)))


(def code-first (t/simple-1-in-1-out-instruction :code "first" #(if (seq? %) (first %) %)))


(def code-fromboolean (t/simple-item-to-code-instruction :boolean))


(def code-fromchar (t/simple-item-to-code-instruction :char))


(def code-fromfloat (t/simple-item-to-code-instruction :float))


(def code-frominteger (t/simple-item-to-code-instruction :integer))


(def code-fromstring (t/simple-item-to-code-instruction :string))


(def code-frominteger
  (core/build-instruction
    code-frominteger
    :tags #{:complex :base :conversion}
    (d/consume-top-of :integer :as :arg)
    (d/push-onto :code :arg)))


(def code-if
  (core/build-instruction
    code-if
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/consume-top-of :boolean :as :which)
    (d/calculate [:which :arg1 :arg2] #(if %1 %2 %3) :as :that)
    (d/push-onto :exec :that)))


(def code-length
  (core/build-instruction
    code-length
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg)
    (d/calculate [:arg] #(if (coll? %1) (count %1) 1) :as :len)
    (d/push-onto :integer :len)))


(def code-list (t/simple-2-in-1-out-instruction :code "list" #(list %1 %2)))


(def code-member?
  (core/build-instruction
    code-member?
    :tags #{:complex :predicate :base}
    (d/consume-top-of :code :as :arg1)
    (d/consume-top-of :code :as :arg2)
    (d/calculate [:arg1] #(if (coll? %1) %1 (list %1)) :as :list1)
    (d/calculate [:list1 :arg2] #(not (not-any? #{%2} %1)) :as :present)
    (d/push-onto :boolean :present)))


(def code-noop
  (core/build-instruction
    code-noop
    :tags #{:complex :base}))


(def code-null? (t/simple-1-in-predicate :code "null?" #(and (coll? %) (empty? %))))


(def code-quote
  (core/build-instruction
    code-quote
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :arg1)
    (d/push-onto :code :arg1)))


(def code-rest (t/simple-1-in-1-out-instruction 
                    :code 
                    "rest" 
                    #(if (coll? %1) 
                         (rest %1) 
                         (list))))


(def code-size
  (core/build-instruction
    code-size
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1] #(u/count-points %1) :as :size)
    (d/push-onto :integer :size)))


(def code-wrap (t/simple-1-in-1-out-instruction :code "wrap" #(list %1)))


(def classic-code-module
  ( ->  (t/make-module  :code
                        :attributes #{:complex :base})
        t/make-visible 
        t/make-equatable
        t/make-movable
        (t/attach-instruction , code-append)
        (t/attach-instruction , code-atom?)
        (t/attach-instruction , code-cons)
        (t/attach-instruction , code-container)
        (t/attach-instruction , code-contains?)
        (t/attach-instruction , code-do)
        (t/attach-instruction , code-do*)
        (t/attach-instruction , code-do*count)
        (t/attach-instruction , code-do*range)
        (t/attach-instruction , code-do*times)
        (t/attach-instruction , code-first)
        (t/attach-instruction , code-fromboolean)
        (t/attach-instruction , code-fromchar)
        (t/attach-instruction , code-frominteger)
        (t/attach-instruction , code-fromstring)
        (t/attach-instruction , code-fromfloat)
        (t/attach-instruction , code-if)
        (t/attach-instruction , code-length)
        (t/attach-instruction , code-list)
        (t/attach-instruction , code-member?)
        (t/attach-instruction , code-noop)
        (t/attach-instruction , code-null?)
        (t/attach-instruction , code-quote)
        (t/attach-instruction , code-rest)
        (t/attach-instruction , code-size)
        (t/attach-instruction , code-wrap)
        ))
