(ns leiningen.prose
  (:require [prose.core :as pr])) 

(defn prose [project path & [out-path]]
  (if out-path 
    (pr/compile-file path out-path)
    (pr/pretty path)))
