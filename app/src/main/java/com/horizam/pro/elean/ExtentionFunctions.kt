package com.horizam.pro.elean

import java.io.File

val File.extension: String
    get() = name.substringAfterLast('.', "")