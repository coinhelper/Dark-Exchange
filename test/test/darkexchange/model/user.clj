(ns test.darkexchange.model.user
  (:use clojure.contrib.test-is
        test.fixtures.user
        darkexchange.model.user))

(def model "user")

(use-fixtures :once fixture)

(deftest test-first-record
  (is (get-record 1)))

(deftest test-validate-user-name
  (is (validate-user-name "blah") "User with unique name does not validate.")
  (is (not (validate-user-name nil)) "Nil user incorrectly validates.")
  (is (not (validate-user-name "")) "Empty string user incorrectly validates.")
  (is (not (validate-user-name "test-user")) "Non-unique user incorrectly validates.")) 

(defn to-byte-array [nums]
  (byte-array (map byte nums))) 

(deftest test-validate-passwords
  (is (validate-passwords (byte-array []) (byte-array []))
    "Empty string passwords do not validate.")
  (is (validate-passwords (to-byte-array [1]) (to-byte-array [1]))
    "One byte passwords do not validate.")
  (is (validate-passwords (to-byte-array [1 2]) (to-byte-array [1 2]))
    "Two byte passwords do not validate.")
  (is (validate-passwords (to-byte-array [1 2 3]) (to-byte-array [1 2 3]))
    "Three byte passwords do not validate.")
  (is (not (validate-passwords (to-byte-array [1 2 3]) (to-byte-array [1 2 4])))
    "Unmatched passwords incorrectly validate."))

(deftest test-create-user
  (let [test-user-name "blah"
        user-id (create-user test-user-name (.getBytes "password" "UTF-8"))]
    (is user-id "User id not returned.")
    (let [user (get-record user-id)]
      (is user (str "User with the id: " user-id " could not be found."))
      (is (= (:name user) test-user-name) "The user name does not match the test user name.")
      (is (not (contains? user :password)) "The password was not removed before saving.")
      (is (:encrypted_password user) "The encrypted password was not set.")
      (is (:salt user) "The salt was not set.")
      (is (:public_key user) "The public key was not set.")
      (is (:public_key_algorithm user) "The public key algorithm was not set.")
      (is (:private_key user) "The private key was not set.")
      (is (:private_key_algorithm user) "The private key algorithm was not set."))
    (when user-id
      (destroy-record { :id user-id }))))

(deftest test-login
  (is (nil? (current-user)) "Started test with a logged in user.") 
  (let [test-user-name "blah"
        test-password "password"
        test-password-bytes (.getBytes test-password "UTF-8")
        user-id (create-user test-user-name test-password-bytes)]
    (is (login test-user-name test-password-bytes) "The user failed to log in.")
    (let [test-current-user (current-user)]
      (is test-current-user "The user was not logged in.")
      (is (= (:name test-current-user) "blah") "The wrong user was logged in.")
      (is (:encrypted_password test-current-user) "No encrypted password was loaded.")
      (is (:salt test-current-user) "No salt was loaded.")
      (is (:password test-current-user) "The password was not saved.")
      (is (:public_key test-current-user) "The public key was not saved.")
      (is (:public_key_algorithm test-current-user) "The public key algorithm was not saved.")
      (is (:private_key test-current-user) "The private key was not saved.")
      (is (:private_key_algorithm test-current-user) "The private key algorithm was not saved.")) 
    (when user-id
      (destroy-record { :id user-id })))
  (logout)
  (is (nil? (current-user)) "The user failed to logout."))