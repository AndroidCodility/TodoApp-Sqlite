package com.codility.todoapp.model

import java.io.Serializable

/**
 * Created by Govind on 2/14/2018.
 */
class Todo(var name: String) : Serializable {
    var id: Int? = null
    var title: String? = null
    var desc: String? = null
    var timestamp: String? = null

    constructor(id: Int, title: String, desc: String, timestamp: String) : this(title) {
        this.id = id
        this.title = title
        this.desc = desc
        this.timestamp = timestamp
    }

    constructor(title: String, desc: String) : this(title) {
        this.title = title
        this.desc = desc
    }
}