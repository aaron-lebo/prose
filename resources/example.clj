(defn hello [name & :keys [city] :or {city "Dallas"}]  
  (println (+ (count name) 1))  ;; because we can  
  (let [message (string/join " " ["Hello," name "from" city])] 
    (println message)  
    message))


(hello "Howard Hughes" "Houston")
(hello "Eleanor Roosevelt" "New York City")
(hello "The Linux Users Group @ UT Dallas")