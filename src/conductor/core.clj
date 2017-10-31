(ns conductor.core
  (require [conductor.output :as c.out]
           [conductor.input :as c.input])
  (:gen-class))

(defn make-app []
  {:output-handler (c.out/make-output)})

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [output-processor (c.out/make-output)
        listener (c.input/make-listener)]
    (c.out/send-string output-processor ".")
    (println "Sleeping for 5000 milis")
    (Thread/sleep 5000)
    (println "Listening...")
    (while true
      (let [speech (c.input/listen listener)]
        (println "Heard" speech)))))
