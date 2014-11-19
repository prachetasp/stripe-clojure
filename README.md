stripe-clojure
================================

stripe-clojure is intended as a lightweight wrapper around the [Stripe API](https://stripe.com/docs/api "Stripe API Documentation")

Usage
================================

stripe-clojure attempts to leverage functional style by allowing the user to call actions on map's. The action's names are those found in the Stripe API docs e.g. (retrieve, create, delete, update etc.) The data contained in each map also uses the same naming scheme found in the Stripe docs.

The first step is to add the library to the project.clj file:

```
[prachetasp/stripe-clojure "1.0.0"]
```

Everything is located in the `stripe-clojure.core` namespace. Add to your namespace declaration:

```
(:require [stripe-clojure.core :as s])
```

The authentication token must be set so it can be used in calls to the API. stripe-clojure currently only supports using the private token. To set the token:

``` 
(set-tokens! {:private "sk_test_fkjdskfjdskfjdslkf"})
```

Once the token has been set you can execute operations against the API.

Currently supported operations are: `cancel`, `create`, `delete`, `retrieve`, `list`, and `update`

These operations accept a map of a map of data keyed by the resource. For example:

```
{:customers {:customer_id "cus_5An5UPQPrSaS9e", :email "mrclojure@stripetest.com", :description "customer test"}}
```

Currently supported resources are: `cards`, `customers`, `events`, `invoices`, `plans`, `subscriptions`, and `tokens`

Putting it all together to update an existing user:

```
(s/update {:customers {:customer_id "cus_5An5UPQPrSaS9e", :email "mrclojure@stripetest.com", :description "customer test"}})
```

The details of the data vary for different resources and operations but the names (keys in the map) are always the same as in the stripe documentation.

A successful request will return a map of the json data returned from Stripe. A failure will return a map containing the type of error and message. The general structure of an error map is:

```
{:error {:type "invalid_request_error", :message "Received unknown parameter: fail", :param "fail"}}
```

Therefore, testing for :error in the response allows you to identify and handle errors.

Contributing
================================

Due to the lightweight nature of the library contributing is easy. Simply make your changes, write a test (if applicable) and submit a PR. The goals of the library are to remain small and fast. Changes that increase performance are encouraged!

To run the tests copy `/test/stripe_clojure/test/core_config.clj.example` to `/test/stripe_clojure/test/core_config.clj` and add the private authentication token to the map `secret-tokens`. core_config.clj is included in the .gitignore file to prevent accidentally leaking tokens.

Tests can then be run with `lein test`.

License
================================

Copyright (C) 2014 Prachetas Prabhu

Distributed under the Eclipse Public License.
