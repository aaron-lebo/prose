(defn buffalo [start end]  
  ;; buffalo  
  (let [strings (repeat (* 1 8) "buffalo")  
      idxs [0 2 6]  
      f (fn [idx itm]  
        (if (some (fn [idx2] (= idx2 idx)) idxs)  
          (string/capitalize itm)  
          itm))  
         
      ;; fn  
      res (map-indexed f strings)]  
    (println res)  
    (str (string/join " " res) (case end  
        :period "."  
        :qmark "?"  
        "!"))  
    ;; res println))  
  


(buffalo :period)
(buffalo :qmark)

(def excited-buffalo (partial buffalo :exmark))
(excited-buffalo)

(= 1 2)

(def sum (partial reduce +))
(println (+ (sum [1 2 3]) 1))

(println) 


(defn [:a 1 & :keys [a] :or {:a 1}]  
  (+ a a)) 


(def + sum)