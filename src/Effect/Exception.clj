(ns Effect.Exception._foreign
  (:refer-clojure :exclude [name])
  (:import [clojure.lang ExceptionInfo]
           [java.io StringWriter PrintWriter]))

(defn showErrorImpl [^Exception err]
  (with-open [^StringWriter sw (StringWriter.)
              ^PrintWriter pw (PrintWriter. sw)]
    (.printStackTrace err pw)
    (str sw)))

(defn error [msg]
  (ex-info msg {}))

(defn message [^Exception err]
  (.getMessage err))

(defn name [err]
  (.getSimpleName (class err)))

(defn stackImpl [just]
  (fn [nothing]
    (fn [^Exception err]
      (if (pos? (count (.getStackTrace err)))
        (just (showErrorImpl err))
        nothing))))

(defn throwException [^Exception err]
  (fn [& _]
    (throw err)))

(defn catchException [c]
  (fn [t]
    (fn [& _]
      (try (t nil)
           (catch ExceptionInfo e
             ((c e) nil))
           (catch Exception e
             ((c (ex-info (.getMessage e) {})) nil))))))
