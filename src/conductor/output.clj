(ns conductor.output
  (import [java.awt Robot]
          [java.awt.event KeyEvent]))

(defn make-output []
  {:robot (new Robot)})

(defn keytype [robot str]
  (doseq [ch str]
    (if (Character/isUpperCase ch)
      (doto robot
        (.keyPress (. KeyEvent VK_SHIFT))
        (.keyPress (int ch))
        (.keyRelease (int ch))
        (.keyRelease (. KeyEvent VK_SHIFT)))
      (let [upCh (Character/toUpperCase ch)]
        (doto robot
          (.keyPress (int upCh))
          (.keyRelease (int upCh)))))))

(def ^:private special-keys
  {::keys:escape (. KeyEvent VK_ESCAPE)
   ::keys:enter (. KeyEvent VK_ENTER)})

(defn send-string [{:keys [robot]} text]
  (keytype robot text))

(defn- with-key-pressed-fn [{:keys [robot]} type body-fn]
  (let [keycode (get special-keys type)]
    (.keyPress robot keycode)
    (body-fn)
    (.keyRelease robot keycode)))

(defmacro with-key-pressed [output type & body]
  `(with-key-pressed-fn ~output ~type (fn [] ~@body)))

(defn send-special [{:keys [robot]} type]
  (if-let [keycode (get special-keys type)]
    (doto robot
      (.keyPress keycode)
      (.keyRelease keycode))))
