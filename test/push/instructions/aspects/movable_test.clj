(ns push.instructions.aspects.movable-test
  (:require [push.interpreter.core :as i]
            [push.interpreter.templates.minimum :as m])
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.instructions.aspects)
  (:use push.type.core)
  (:use push.util.numerics)
  (:use push.instructions.aspects.movable)
  )


;; support functions

(fact "scalar-to-index returns a valid index"
  (scalar-to-index 10 7) => 3
  (scalar-to-index -10 7) => 4
  (scalar-to-index -10.5 7) => 3.0
  (scalar-to-index 87/7 6) => 0N
  (scalar-to-index 94/7 6) => 1N
  (scalar-to-index 87/7 1) => 0N
  (scalar-to-index 77777777777777777777777777777777777777777M 8) => 1N
  (scalar-to-index 77777777777777777777777777777777777777777N 8) => 1N
  (scalar-to-index 76652.3333e871M 17) => 9N
  (scalar-to-index -76652.3333e871M 17) => 8N
  (scalar-to-index ∞ 17) => 0
  (scalar-to-index -∞ 17) => 0
  (scalar-to-index -1.2246467991473532E-16 4) => 0
  )



;; movable instructions


(fact "dup-instruction returns an Instruction with the correct stuff"
  (let [foo-dup (dup-instruction (make-type :foo))]
    (class foo-dup) => push.instructions.core.Instruction
    (:tags foo-dup) => #{:combinator}
    (:needs foo-dup) => {:foo 1}
    (:token foo-dup) => :foo-dup
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 2)}) foo-dup)
        :foo-dup)
      :foo) => '(1 1 2)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(11)}) foo-dup)
        :foo-dup)
      :foo) => '(11 11)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '()}) foo-dup)
        :foo-dup)
      :foo) => '()))


(fact "flush-instruction returns an Instruction with the correct stuff"
  (let [foo-flush (flush-instruction (make-type :foo))]
    (class foo-flush) => push.instructions.core.Instruction
    (:tags foo-flush) => #{:combinator}
    (:needs foo-flush) => {}
    (:token foo-flush) => :foo-flush
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(false 11)}) foo-flush)
        :foo-flush)
      :foo) => '()
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '()}) foo-flush)
        :foo-flush)
      :foo) => '()))


(fact "pop-instruction returns an Instruction with the correct stuff"
  (let [foo-pop (pop-instruction (make-type :foo))]
    (class foo-pop) => push.instructions.core.Instruction
    (:tags foo-pop) => #{:combinator}
    (:needs foo-pop) => {}
    (:token foo-pop) => :foo-pop
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(11 22)}) foo-pop)
        :foo-pop)
      :foo) => '(22)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '()}) foo-pop)
        :foo-pop)
      :foo) => '()))


(fact "rotate-instruction returns an Instruction with the correct stuff"
  (let [foo-rotate (rotate-instruction (make-type :foo))]
    (class foo-rotate) => push.instructions.core.Instruction
    (:tags foo-rotate) => #{:combinator}
    (:needs foo-rotate) => {:foo 3}
    (:token foo-rotate) => :foo-rotate
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(11 22 33 44)}) foo-rotate)
        :foo-rotate)
      :foo) => '(33 11 22 44)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(11 22 33)}) foo-rotate)
        :foo-rotate)
      :foo) => '(33 11 22)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(11 22)}) foo-rotate)
        :foo-rotate)
      :foo) => '(11 22)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '()}) foo-rotate)
        :foo-rotate)
      :foo) => '()))



(fact "shove-instruction returns an Instruction with the correct stuff"
  (let [foo-shove (shove-instruction (make-type :foo))]
    (class foo-shove) => push.instructions.core.Instruction
    (:tags foo-shove) => #{:combinator}
    (:needs foo-shove) => {:foo 1, :scalar 1}
    (:token foo-shove) => :foo-shove
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c) :scalar '(1)}) foo-shove)
        :foo-shove)
      :foo) => '(:b :a :c)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c) :scalar '(0)}) foo-shove)
        :foo-shove)
      :foo) => '(:a :b :c)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c) :scalar '(2)}) foo-shove)
        :foo-shove)
      :foo) => '(:b :c :a)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c) :scalar '(-1)}) foo-shove)
        :foo-shove)
      :foo) => '(:a :b :c)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c) :scalar '(-1182)}) foo-shove)
        :foo-shove)
      :foo) => '(:a :b :c)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c :d :e) :scalar '(387)}) foo-shove)
        :foo-shove)
      :foo) => '(:b :c :d :e :a)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c :d :e) :scalar '(38702777777777777744444444444444444444444N)}) foo-shove)
        :foo-shove)
      :foo) => '(:b :c :d :e :a)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c :d :e) :scalar '(-38702777777777777744444444444444444444444N)}) foo-shove)
        :foo-shove)
      :foo) => '(:a :b :c :d :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c :d :e) :scalar '(387/13)}) foo-shove)
        :foo-shove)
      :foo) => '(:b :c :d :e :a)    
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c :d :e) :scalar '(7.71753612845e612M)}) foo-shove)
        :foo-shove)
      :foo) => '(:b :c :d :e :a)
    ))


(fact "swap-instruction returns an Instruction with the correct stuff"
  (let [foo-swap (swap-instruction (make-type :foo))]
    (class foo-swap) => push.instructions.core.Instruction
    (:tags foo-swap) => #{:combinator}
    (:needs foo-swap) => {:foo 2}
    (:token foo-swap) => :foo-swap
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c)}) foo-swap)
        :foo-swap)
      :foo) => '(:b :a :c)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b)}) foo-swap)
        :foo-swap)
      :foo) => '(:b :a)))


(fact "yank-instruction returns an Instruction with the correct stuff"
  (let [foo-yank (yank-instruction (make-type :foo))]
    (class foo-yank) => push.instructions.core.Instruction
    (:tags foo-yank) => #{:combinator}
    (:needs foo-yank) => {:foo 1, :scalar 1}
    (:token foo-yank) => :foo-yank
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(2)}) foo-yank)
        :foo-yank)
      :foo) => '(:c :a :b :d :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(0)}) foo-yank)
        :foo-yank)
      :foo) => '(:a :b :c :d :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(-1)}) foo-yank)
        :foo-yank)
      :foo) => '(:a :b :c :d :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(4)}) foo-yank)
        :foo-yank)
      :foo) => '(:e :a :b :c :d)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(19122)}) foo-yank)
        :foo-yank)
      :foo) => '(:e :a :b :c :d)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(-8.555)}) foo-yank)
        :foo-yank)
      :foo) => '(:a :b :c :d :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(19122/17)}) foo-yank)
        :foo-yank)
      :foo) => '(:e :a :b :c :d)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(5/2)}) foo-yank)
        :foo-yank)
      :foo) => '(:d :a :b :c :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(-666666666666.55555M)}) foo-yank)
        :foo-yank)
      :foo) => '(:a :b :c :d :e)

    ))



(fact "yankdup-instruction returns an Instruction with the correct stuff"
  (let [foo-yankdup (yankdup-instruction (make-type :foo))]
    (class foo-yankdup) => push.instructions.core.Instruction
    (:tags foo-yankdup) => #{:combinator}
    (:needs foo-yankdup) => {:foo 1, :scalar 1}
    (:token foo-yankdup) => :foo-yankdup
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(2)}) foo-yankdup)
        :foo-yankdup)
      :foo) => '(:c :a :b :c :d :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(0)}) foo-yankdup)
        :foo-yankdup)
      :foo) => '(:a :a :b :c :d :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(-1)}) foo-yankdup)
        :foo-yankdup)
      :foo) => '(:a :a :b :c :d :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(4)}) foo-yankdup)
        :foo-yankdup)
      :foo) => '(:e :a :b :c :d :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(912)}) foo-yankdup)
        :foo-yankdup)
      :foo) => '(:e :a :b :c :d :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(9.7e82)}) foo-yankdup)
        :foo-yankdup)
      :foo) => '(:e :a :b :c :d :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(912/7)}) foo-yankdup)
        :foo-yankdup)
      :foo) => '(:e :a :b :c :d :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(-912876.5512M)}) foo-yankdup)
        :foo-yankdup)
      :foo) => '(:a :a :b :c :d :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(12/7)}) foo-yankdup)
        :foo-yankdup)
      :foo) => '(:c :a :b :c :d :e)
    ))


(fact "`make-movable` adds the :movable attribute to a PushType record"
  (:attributes (make-movable (make-type :foo))) => #{:movable})


(fact "`make-movable` takes adds appropriate instructions to a PushType record"
  (keys (:instructions
    (make-movable (make-type :foo)))) =>
      (contains [:foo-dup :foo-flush :foo-pop :foo-rotate :foo-shove :foo-swap :foo-yank :foo-yankdup :foo-againlater] :gaps-ok :in-any-order))
