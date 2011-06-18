(ns darkexchange.controller.offer.has-panel
  (:require [darkexchange.controller.offer.widgets :as offer-widgets]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.model.currency :as currency-model]
            [darkexchange.model.payment-type :as payment-type-model]
            [seesaw.core :as seesaw-core]))

(defn find-i-have-amount [parent-component]
  (seesaw-core/select parent-component ["#i-have-amount"]))

(defn find-i-have-currency-combobox [parent-component]
  (seesaw-core/select parent-component ["#i-have-currency"]))

(defn find-i-have-payment-type-combobox [parent-component]
  (seesaw-core/select parent-component ["#i-have-payment-type"]))

(defn i-have-amount [parent-component]
  (Integer/parseInt (seesaw-core/text (find-i-have-amount parent-component))))

(defn i-have-currency [parent-component]
  (:currency (seesaw-core/selection (find-i-have-currency-combobox parent-component))))

(defn i-have-payment-type [parent-component]
  (:payment-type (seesaw-core/selection (find-i-have-payment-type-combobox parent-component))))

(defn has-offer [parent-component]
  { :has_amount (i-have-amount parent-component)
    :has_currency (:code (i-have-currency parent-component))
    :has_payment_type (:code (i-have-payment-type parent-component)) })

(defn load-currencies [parent-component]
  (offer-widgets/load-currencies (find-i-have-currency-combobox parent-component))
  parent-component)

(defn load-payment-types [parent-component]
  (offer-widgets/load-payment-type-combobox (find-i-have-payment-type-combobox parent-component)
    (find-i-have-currency-combobox parent-component))
  parent-component)

(defn load-data [parent-component]
  (load-payment-types (load-currencies parent-component)))

(defn attach-currency-listener [parent-component]
  (offer-widgets/attach-currency-listener (find-i-have-currency-combobox parent-component) load-payment-types)
  parent-component)

(defn attach [parent-component]
  (attach-currency-listener parent-component))