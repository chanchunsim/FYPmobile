# MOA_group_assignment

# Note for CRUD Firestore Database
[An Internal Link](https://firebase.google.com/docs/firestore/rtdb-vs-firestore)

## To get user id
```kotlin
 val uid:String = FirebaseAuth.getInstance().currentUser!!.uid;
 val email:String = FirebaseAuth.getInstance().currentUser!!.email.toString();
```

## To create key and value variable
```kotlin
val user:MutableMap<String, String> = HashMap()
user["uid"] = uid;
user['name'] = email;
```
--- or ---

```kotlin
val user = hashMapOf<String, Any>(
    "email" to email,
    "name" to "Cham Zhao Si",
    "address" to "30-11 Jalan Daud",
    "gender" to "mela"
)
```

## To set the document 
```kotlin
mDB.collection("Users").document(uid)
```

## To create the document with a unique id
```kotlin
mDB.collection("Users")
            .add(user as Map<String, Any>)
            .addOnCompleteListener {
                Toast.makeText(this@Dashboard, "record added successfully", Toast.LENGTH_SHORT).show()
            }
```

## To create the collection with own document 
```kotlin
mDB.collection("Users").document(uid)
            .set(user)
            .addOnCompleteListener {
                Toast.makeText(this@Dashboard, "record added successfully", Toast.LENGTH_SHORT).show()
            }
```

## To update the data inside a specific document id
```kotlin
mDB.collection("Users").document(uid)
            .update(user as Map<String, Any>)
            .addOnCompleteListener {
                Toast.makeText(this@Dashboard, "record added successfully", Toast.LENGTH_SHORT).show()
            }
```

## To delete the data inside a specific document id
```kotlin
val user = hashMapOf<String, Any>(
            "email" to FieldValue.delete()
        )

        mDB.collection("Users")
            .document(uid)
            .update(user as Map<String, Any>)
            .addOnCompleteListener {
                Toast.makeText(this@Dashboard, "record added successfully", Toast.LENGTH_SHORT).show()
            }
```

## To retrieve data from every document
```kotlin
mDB.collection("Users")
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    for(document in it.result!!){
                        Log.d("Result", document.toString())
                        Log.d("Result", document.data.getValue("name").toString())
                    }
                }
            }
```

## To retrieve data from a specific document
```kotlin
mDB.collection("Users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                    if (document != null){
                        Log.d("Result", document.data.toString())
                    }

            }
```

## To retrieve data with a condition
```kotlin
mDB.collection("Users")
            .whereEqualTo("name", "Cham Zhao Si")
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    if (document != null){
                        Log.d("Result", document.data.toString())
                    }
                }
            }
```



## To delete a document
```kotlin
mDB.collection("Users")
            .document("GKJ8PkTBrFrx1rXK04wf")
            .delete()
            .addOnCompleteListener {
                Toast.makeText(this@Dashboard, "record added successfully", Toast.LENGTH_SHORT).show()
            }
```

## To check whether the key existing or not
```kotlin
val dataMap : MutableMap<String, Any>? = document.data
                    Log.d("dataMap", dataMap.toString())

                    if(dataMap!!.containsKey("Babi")){
                        Log.d("dataMap", "Babi is existing")
                    }else{
                        Log.d("dataMap", "Babi is not existing")
                    }
```

## Remark
```kotlin
val alovelaceDocumentRef = db.document("users/alovelace")
```
```kotlin
val messageRef = db
        .collection("rooms").document("roomA")
        .collection("messages").document("message1")
```



### set() -> will create, merge and overwrite existing one document data, it is DocumentReference
### add() -> will create a new document will a unique id, it is CollectionReference,
[An Internal Link](https://stackoverflow.com/questions/47474522/firestore-difference-between-set-and-add/#answers)

### update() -> will update data to a specific document id


