(ns clodustry.core
  (:require
   [reagent.core :as r]
   [ajax.core :as ajax :refer [GET]]))



;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ;; Experimentation

;; (def tdata
;;   [{:author "Author"
;;     :description "Short description of the mod."
;;     :name "Name Of Mod"
;;     :repository "Author/Name-Of-Mod"}
;;    {:author "2Author2"
;;     :description "2Short description of the mod.2"
;;     :name "2Name Of Mod2"
;;     :repository "2Author/Name-Of-Mod2"}])

;; (def row-data
;;   {:author "Not Me"
;;    :description "Short description of the mod."
;;    :name "Name Of Mod"
;;    :repository "Author/Name-Of-Mod"})


;; (defn atom-input [value]
;;   [:input {:type "text"
;;            :value @value
;;            :on-change #(reset! value (-> % .-target .-value))}])

;; (defn shared-state []
;;   (let [val (r/atom "foo")]
;;     (fn []
;;       [:div
;;        [:p "The value is now: " @val]
;;        [:p "Change it here: " [atom-input val]]])))

;; ;; this works, but it's insane...
;; ;; (aget (.parse js/JSON "{ \"text\": \"whoo\" }") "text")

;; ;; this works, but it's insane...
;; ;; (.-text (.parse js/JSON "{ \"text\": \"whoo\" }"))

;; ;; ...this is supposed to work?
;; ;; (-> (.parse js/JSON "{ \"text\": { \"a\": \"whoo\" } }") .-text .-whoo)

;; ;; ...what the fucking actual hell....
;; ;; (.. (.parse js/JSON "{ \"text\": { \"a\": \"whoo\" } }") -text -whoo)

;; ;; I'm certainly about to go insane. 
;; ;; (def json "{\"foo\": 1, \"bar\": 2, \"baz\": [1,2,3]}")
;; ;; (def a (.parse js/JSON json))
;; ;; (.log js/console a)

;; (def data-as-str-small
;;   "{:dt [2017 6 30], :cashflow 431782}")

;; (def data-as-str 
;;   "[{:dt [2017 6 30], :cashflow \"431782\"}
;;     {:dt [2018 6 30], :cashflow 452271}
;;     {:dt [2019 6 30], :cashflow 473785}
;;     {:dt [2020 6 30], :cashflow 496374}]")

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ;; Loading

;; ;; ;; JSON string data dumped by Python.
;; ;; (def data
;; ;;   (js->clj
;; ;;    (.parse js/JSON js/rawData)
;; ;;    :keywordize-keys true))


;; ;; (defn handle-data
;; ;;   [response]
;; ;;   (js->clj (.parse js/JSON (str response))
;; ;;            :keywordize-keys true))

;; ;; (def data-atom (r/atom []))

;; ;; (defn handler-atom [response]
;; ;;   (reset! data-atom (:body response)))

;; ;; (defn handler [response]
;; ;;   (.log js/console "------------" (response)))

;; ;; (defn error-handler [{:keys [status status-text]}]
;; ;;   (.log js/console (str "something bad happened: " status " " status-text)))

;; ;; ;; (defn handler [response]
;; ;; ;;   (str response))

;; JSON string data dumped by Python, inside a JS string.
(def data
  (js->clj js/rawData
           :keywordize-keys false))

;; ;; (defn get-data []
;; ;;   (GET "/data.json" {:handler handler-atom
;; ;;                      :error-handler error-handler
;; ;;                      :response-format :json}))

;; ;; (.log js/console @data-atom)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helpers

(defn map-tag [tag xs]
  (map (fn [x] [tag x]) xs))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Views

(defn home-page []
  [:div [:h2 "Welcome to Reagent"]])

(defn header []
  [:header
   [:a {:href "/"}
    [:h1 "Mindustry Mods"]]])

(defn footer []
  [:footer
   [:a {:href "https://github.com/SimonWoodburyForget/mindustry-mods/blob/master/CONTRIBUTING.md#adding-mods-to-the-listing"}
    "Adding mods to this list."]])      

;; (defn map-headers
;;   [headers col]
;;   (map (fn [h] [:td (h col)]) headers))

;; (defn make-tr
;;   [headers data]
;;   (map-tag :tr (for [x data]
;;                  (map-headers headers x))))

(defn icon [m]
  "Returns icon tag with image and src url if icon exists."
  [:icon [:img
    (if (clojure.string/blank? (m "icon")) {}
        {:src (m "icon_url")})]])

(defn description [m]
  [:p.description
   (m "description")])

(defn endpoint [m]
  [:a {:href (m "endpoint")}
   (m "name")])

(defn make-rows
  [mods]
  (for [m mods]
    [:tr
     [:td {:class "metadata"}
      [:p
       (icon m)
       (endpoint m)
       ]]]))

(defn table
  [headers data]
  [:table
   [:thead
    [:tr (map-tag :th headers)]]
   [:tbody (make-rows data)]])

(defn listing []
  (table ["metadata"] data))

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ;; Mod



;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ;; Initialize app



(defn mount-root []
  (r/render [listing] (.getElementById js/document "app")))

(defn init! []
   (mount-root))


;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ;; Testing 
;; (defonce timer (r/atom (js/Date.)))

;; (defonce time-color (r/atom "#f34"))

;; (defonce time-updater (js/setInterval
;;                        #(reset! timer (js/Date.)) 1000))

;; (defn greeting [message]
;;   [:h1 message])

;; (defn clock []
;;   (let [time-str (-> @timer .toTimeString (clojure.string/split " ") first)]
;;     [:div.example-clock
;;      {:style {:color @time-color}}
;;      time-str]))

;; (defn color-input []
;;   [:div.color-input
;;    "Time color: "
;;    [:input {:type "text"
;;             :value @time-color
;;             :on-change #(reset! time-color (-> % .-target .-value))}]])

;; (defn simple-example []
;;   [:div
;;    [greeting "Hello world, it is now"]
;;    [clock]
;;    [color-input]])

