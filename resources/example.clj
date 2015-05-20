(defn buffalo [end]  
  (let [strings (repeat (* 1 8) "buffalo")  
      idxs [0 2 6]  
      f (fn idx [idx itm]  
        (if (some (fn [idx2] (= idx2 idx)) idxs)  
          (string/capitalize itm)  
          itm)) 
       
      res (map-indexed f strings)] 
    (str (string/join " " res) (case end  
        period "."  
        qmark "?"  
        "!"))))


(buffalo :period)
(buffalo :qmark)

(def excited-buffalo (partial buffalo :exmark))
(excited-buffalo)