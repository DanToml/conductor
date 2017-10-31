(ns conductor.input
  (import [edu.cmu.sphinx.frontend.util Microphone]
          [edu.cmu.sphinx.recognizer Recognizer]
          [edu.cmu.sphinx.result Result]
          [edu.cmu.sphinx.util.props ConfigurationManager]))

(defn new-config
  ([] (new-config "conductor.config.xml"))
  ([config-path] (new ConfigurationManager (clojure.java.io/resource config-path))))

(defn- get-recognizer [config]
  (let [recognizer (.lookup config "recognizer")]
    (do
      (.allocate recognizer)
      recognizer)))

(defn- get-microphone [config]
  (let [microphone (.lookup config "microphone")]
    (do
      (.startRecording microphone)
      microphone)))

(defn- get-grammar [config]
  (some-> config (.lookup "jsgfGrammar") .getRuleGrammar))

(defn make-listener
  ([] (make-listener (new-config)))
  ([config] {:recognizer (get-recognizer config)
             :microphone (get-microphone config)
             :grammar (get-grammar config)}))

(defn- sphinx-result->map [grammar result]
  (let [text (.getBestResultNoFiller result)]
    {:text text
     :tokens (.getTokens (.getActiveTokens result))
     :parsed (.parse grammar text nil)}))

(defn- ensure-final [result]
  (if (.isFinal result)
    result
    nil))

(defn listen [{:keys [recognizer microphone grammar]}]
  (some->> recognizer .recognize ensure-final (sphinx-result->map grammar)))

