(set-env!
 :source-paths #{"src/cljs"}
 :resource-paths #{"html"}
 :dependencies '[[adzerk/boot-cljs "2.0.0"]
                 [org.clojure/clojurescript "1.9.518"]
                 [crisptrutski/boot-cljs-test "0.3.0"]])

(require '[adzerk.boot-cljs :refer [cljs]])
(require '[crisptrutski.boot-cljs-test :refer [test-cljs]])

(deftask testing [] (merge-env! :source-paths #{"test/cljs"}) identity)
