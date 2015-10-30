(ns push.instructions.base.integer_test
  (:require [push.interpreter.interpreter-core :as i])
  (:use [push.instructions.base.integer])
  (:use midje.sweet)
  (:use [push.util.test-helpers]))


;; fixtures


(def knows-some-integers
    (i/make-interpreter 
      :stacks {:integer '(11 -5 3333333333333333333 7777777777777777777)}))
      ;; the last two, when added, push us over the interger overflow limit


; setup-stack items instruction read-stack


(tabular
  (fact ":integer-add returns the sum, auto-promoting overflows"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) ?arrow ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack   ?arrow   ?expected
    ;; adding
    :integer    '(11 -5)  integer-add   :integer     =>       '(6)
    :integer    '(-3 -5)  integer-add   :integer     =>       '(-8)
    ;; missing args
    :integer    '(11)     integer-add   :integer     =>       '(11)
    :integer    '()       integer-add   :integer     =>       '()
    ;; overflow
    :integer    '(3333333333333333333 7777777777777777777)
                          integer-add   :integer     =>       '(11111111111111111110N))


(tabular
  (fact ":integer-subtract returns (- :second :first)"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) ?arrow ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack   ?arrow   ?expected
    ;; just the math
    :integer    '(11 -5)  integer-subtract   :integer     =>       '(16)
    :integer    '(-3 -5)  integer-subtract   :integer     =>       '(2)
    ;; missing args
    :integer    '(11)     integer-subtract   :integer     =>       '(11)
    :integer    '()       integer-subtract   :integer     =>       '()
    ;; overflow
    :integer    '(33333333333333333333 77777777777777777777)
                          integer-subtract   :integer     =>       '(-44444444444444444444N))


(tabular
  (fact ":integer-multiply returns the product, auto-promoting overflows"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) ?arrow ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack   ?arrow   ?expected
    ;; just the math
    :integer    '(11 -5)  integer-multiply   :integer     =>       '(-55)
    :integer    '(-3 -5)  integer-multiply   :integer     =>       '(15)
    ;; missing args
    :integer    '(11)     integer-multiply   :integer     =>       '(11)
    :integer    '()       integer-multiply   :integer     =>       '()
    ;; overflow
    :integer    '(333333333333 777777777777)
                          integer-multiply   :integer     =>       '(259259259258740740740741N))



(tabular
  (fact ":integer-divide returns the quotient :second/:first, unless :first is zero"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) ?arrow ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack   ?arrow   ?expected
    ;; just the math
    :integer    '(4 20)   integer-divide      :integer     =>       '(5)
    :integer    '(-3 -15) integer-divide      :integer     =>       '(5)
    ;; missing args
    :integer    '(11)     integer-divide      :integer     =>       '(11)
    :integer    '()       integer-divide      :integer     =>       '()
    ;; divide-by-zero
    :integer    '(0 11)   integer-divide      :integer     =>       '(0 11))


(tabular
  (fact ":integer-mod returns (mod :second :first)"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) ?arrow ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack   ?arrow   ?expected
    ;; just the math
    :integer    '(4 20)   integer-mod      :integer     =>       '(0)
    :integer    '(4 21)   integer-mod      :integer     =>       '(1)
    :integer    '(4 -21)  integer-mod      :integer     =>       '(3)
    :integer    '(-4 21)  integer-mod      :integer     =>       '(-3)
    :integer    '(-3 -15) integer-mod      :integer     =>       '(0)
    :integer    '(-3 -16) integer-mod      :integer     =>       '(-1)
    ;; missing args
    :integer    '(11)     integer-mod      :integer     =>       '(11)
    :integer    '()       integer-mod      :integer     =>       '()
    ;; divide-by-zero
    :integer    '(0 11)   integer-mod      :integer     =>       '(0 11))



(tabular
  (fact ":integer-lt returns a :boolean indicating whether :first < :second"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) ?arrow ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack   ?arrow   ?expected
    ;; just the math
    :integer    '(4 20)    integer-lt      :boolean     =>       '(true)
    :integer    '(20 4)    integer-lt      :boolean     =>       '(false)
    :integer    '(4 4)     integer-lt      :boolean     =>       '(false)
    ;; missing args 
    :integer    '(11)      integer-lt      :boolean     =>       '()
    :integer    '(11)      integer-lt      :integer     =>       '(11)
    :integer    '()        integer-lt      :boolean     =>       '()
    :integer    '()        integer-lt      :integer     =>       '())


(tabular
  (fact ":integer-gt returns a :boolean indicating whether :first > :second"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) ?arrow ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack   ?arrow   ?expected
    ;; just the math
    :integer    '(4 20)    integer-gt      :boolean     =>       '(false)
    :integer    '(20 4)    integer-gt      :boolean     =>       '(true)
    :integer    '(4 4)     integer-gt      :boolean     =>       '(false)
    ;; missing args 
    :integer    '(11)      integer-gt      :boolean     =>       '()
    :integer    '(11)      integer-gt      :integer     =>       '(11)
    :integer    '()        integer-gt      :boolean     =>       '()
    :integer    '()        integer-gt      :integer     =>       '())



(tabular
  (fact ":integer-eq returns a :boolean indicating whether :first = :second"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) ?arrow ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack   ?arrow   ?expected
    ;; just the math
    :integer    '(4 20)    integer-eq      :boolean     =>       '(false)
    :integer    '(20 4)    integer-eq      :boolean     =>       '(false)
    :integer    '(4 4)     integer-eq      :boolean     =>       '(true)
    ;; missing args 
    :integer    '(11)      integer-eq      :boolean     =>       '()
    :integer    '(11)      integer-eq      :integer     =>       '(11)
    :integer    '()        integer-eq      :boolean     =>       '()
    :integer    '()        integer-eq      :integer     =>       '())

