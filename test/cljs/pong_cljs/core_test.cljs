(ns pong-cljs.core-test
  (:require-macros [cljs.test :refer [deftest testing is async]])
  (:require [cljs.test]
            [pong-cljs.core :as core]))

(deftest something []
  (testing "first test"
    (is (= 1 2))))


