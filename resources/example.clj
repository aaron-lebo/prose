(defn hello [name & :keys [city] :or {city "Dallas"}]  
  (let [message (join " " ["Hello," name "from" city])] 
    (println message)  
    message))
