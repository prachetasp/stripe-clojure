(ns stripe-clojure.core
  "Functions for Stripe Customers API"
  (:require [clj-http.client :as client])
  (:refer-clojure :exclude (list)))

;; TODO: add support for public token selection?
(def stripe-tokens (atom {:public "" :private ""}))
(defn set-tokens! [m] (swap! stripe-tokens (fn [a] (merge a m))))

(def stripe-api-url "https://api.stripe.com/v1/")
(defn build-url [[url-ks url-vs] params]
  (str stripe-api-url
    (apply str
      (interpose "/"
        (filter (comp not nil?)
          (interleave url-ks (map params url-vs)))))))

(defn make-request
  [params method resource]
  (:body (method (build-url resource params)
           {:basic-auth [(:private @stripe-tokens)]
            :query-params (apply dissoc params (second resource))
            :throw-exceptions false
            :as :json
            :coerce :always})))

(def url-vals {"cards" :card_id
               ;;"charges" :charge_id
               ;;"coupons" :coupon_id
               "customers" :customer_id
               "events" :event_id
               ;;"invoiceitems" :invoiceitem_id
               "invoices" :invoice_id
               "plans" :plan_id
               "subscriptions" :subscription_id
               "tokens" :token_id})

;; resources lacking test coverage are commented out
;; map like {:cards [["customers" "cards"] [:customer_id :card_id]]...}
;; second vector values can also be nil
(def url-mapping (into {}
                   (map (fn [[k v]] [k [v (map url-vals v)]])
                     {:cards ["customers" "cards"]
                      ;;:charges ["charges"]
                      ;;:capture ["charges" "capture"] ; no-id endpt
                      ;;:refund ["charges" "refund"] ; no-id endpt
                      ;;:coupons ["coupons"]
                      :customers ["customers"]
                      ;;:discounts ["customers" "discount"] ; no-id endpt
                      :events ["events"]
                      ;;:invoiceitems ["invoiceitems"]
                      :invoices ["invoices"]
                      ;;:lines ["invoices" "lines"] ; no-id endpt
                      ;;:pay ["invoices" "pay"] ; no id endpt
                      ;;:upcoming ["invoices" "upcoming"] ; no-id endpt
                      :plans ["plans"]
                      :subscriptions ["customers" "subscriptions"]
                      :tokens ["tokens"]})))

(defmacro defop
  "Creates function that accepts a map of resource keyword to map of params"
  [op-name http-action]
  `(defn ~op-name [resource#]
     (let [[kw# params#] (first resource#)]
       (make-request params# ~http-action (url-mapping kw#)))))

;; operations lacking test coverage are commented out
(defop cancel client/delete)
#_(defop capture client/post)
(defop create client/post)
(defop delete client/delete)
(defop retrieve client/get)
(defop list client/get)
#_(defop pay client/post)
#_(defop refund client/post)
(defop update client/post)
