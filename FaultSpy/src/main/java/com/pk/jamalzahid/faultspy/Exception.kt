package com.pk.jamalzahid.faultspy


/**
 * @copyright
 * Copyright (c) 2023  M. JAMAL
 * All rights reserved.
 * Created by M. JAMAL on 12/07/2024
 * @author M. JAMAL
 */

class Exception(title:String, stacktrace:Array<StackTraceElement>):Throwable(title) {
    init {
        stackTrace = stacktrace
    }
}