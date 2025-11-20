;;; Copied from nlflame, really should be part of platform
;;; TODO component pin declearations, types

(ns pimento.pins
  (:require [re-frame.core :as rf]
            [hyperphor.multitool.core :as u]
  ))


(rf/reg-sub
 :pin
 (fn [db [_ comp pin]]
   (get-in db [:pins comp pin] )
   ))

(rf/reg-event-db
 :set-pin
 (fn [db [_ comp pin v]]
   (let [wires (get-in db [:wires comp pin])]
     (reduce (fn [db wire]
               (assoc-in db [:pins (first wire) (second wire)] v))
             (assoc-in db [:pins comp pin] v)
             wires))))

(rf/reg-event-db
 :wire
 (fn [db [_ comp-out pin-out comp-in pin-in]]
   (update-in db [:wires comp-out pin-out] conj [comp-in pin-in])))

(defn wire
  [comp-out pin-out comp-in pin-in]
  (rf/dispatch [:wire comp-out pin-out comp-in pin-in ]))

