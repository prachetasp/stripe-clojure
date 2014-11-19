(ns stripe-clojure.test.core
  (:require [stripe-clojure.core :as s])
  (:require [stripe-clojure.test.core-config :refer [secret-tokens]])
  (:require [clojure.test :refer [deftest is testing]]))

(deftest test-set-tokens!
  (testing "set-tokens!"
    (testing "private and public" (is (= (s/set-tokens! {:private "private" :public "public"}) {:private "private" :public "public"})))
    (testing "private only" (is (= (s/set-tokens! {:private "privater"}) {:private "privater" :public "public"})))
    (testing "public only" (is (= (s/set-tokens! {:public "publicer"}) {:private "privater" :public "publicer"})))
    (testing "empty" (is (= (s/set-tokens! {}) {:private "privater" :public "publicer"})))))

(deftest test-build-url
  (is (= (s/build-url [["customers" "cards"] [:customer_id :card_id]]
           {:customer_id "PHL123" :card_id "ORD789"})
        (str s/stripe-api-url "customers/PHL123/cards/ORD789")) "build normal url")
  (is (= (s/build-url [["charges" "refund"] [:charge_id nil]]
           {:customer_id "LAX123" :charge_id "JFK789"})
        (str s/stripe-api-url "charges/JFK789/refund")) "build nil-value url"))

(deftest test-url-mapping
  (is (= (:cards s/url-mapping) [["customers" "cards"] [:customer_id :card_id]]) "test url mapping"))

(def card {:number "4242424242424242"
           :exp_month 12
           :exp_year 2020
           :cvc 123
           :name "Mr. Stripe Clojure"})
(def customer {:card card
               :email "mrclojure@stripetest.com"
               :description "customer test from clj-stripe"})

(s/set-tokens! secret-tokens)
(deftest functional-test
  (testing "Functional Test -"
    (let [{id :id :as c} (s/create {:customers customer})
          c-id {:customer_id id}]
      (testing "create" (is (not (nil? (:id c)))))
      (testing "retrieve" (is (= (s/retrieve {:customers c-id}) c)))
      (testing "update" (is (= (s/update {:customers
                                          (merge c-id {:email "newmrclojure@stripetest.com"})})
                              (s/retrieve {:customers c-id}))))
      (s/delete {:customers c-id})
      (testing "delete and list" (is (every? false?
                                       (map #(= id (:id %))
                                         (:data (s/list {:customers {}})))))))))

(deftest error-message
  (is (contains? (s/create {:customers {:customer_id "xxx1" :fail "test"}}) :error) "error message return"))
