package com.starsoft.skeleton.compose.util


/**
 * Created by Dmitry Starkin on 26.02.2025 14:58.
 */

fun Class<*>.isExtendInterface(type: Class<*>): Boolean {
    this.interfaces.apply {
        for (typeOfApi in this) {
            if (typeOfApi == type) {
                return true
            } else if(typeOfApi.isExtendInterface(type)){
                return true
            }
        }
    }
    return false
}

fun Class<*>.isDirectlyExtendInterface(type: Class<*>): Boolean {
    this.interfaces.apply {
        for (typeOfApi in this) {
            if (typeOfApi == type) {
                return true
            }
        }
    }
    return false
}

fun Class<*>.isInstanceOrExtend(type: Class<*>): Boolean {
    this.getSuperClasses().apply {
        for (typeOfApi in this) {
            if (typeOfApi == type) {
                return true
            }
        }
    }
    return false
}


fun Class<*>.getSuperClasses() :List<Class<*>> {
    val classList = ArrayList<Class<*>>()
    classList.add(this)
    var superclass: Class<*>?  = this.superclass?.apply {this as Class<*> }
    superclass?.apply {classList.add(this)  }
    while (superclass != null) {
        superclass = superclass.superclass?.apply {this as Class<*> }
        superclass?.apply {classList.add(this)  }
    }
    return classList;
}