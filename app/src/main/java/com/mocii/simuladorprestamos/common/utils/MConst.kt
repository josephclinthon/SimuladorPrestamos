package com.mocii.simuladorprestamos.common.utils

object MConst {
    const val GENERIC_LIMIT: Int = 100
    const val MODE_INS = "INS"
    const val MODE_UPD = "UPD"
    const val LEVEL_ONE = 1
    const val LEVEL_ZERO = 0

    const val STATE_LOCAL = 'L'
    const val STATE_REMOTE = 'R'
    const val STATE_DUPLICATE = 'D'
    const val STATE_TO_NONE = 'N'
    const val STATE_CONFIRM = 'C'

    const val EMPTY_STRING = ""
    const val YES = 'S'
    const val NOT = 'N'
    const val SPACE_01:Long = 10000000000000000

    const val DOWNLOADING = "DESCARGANDO"
    const val DOWNLOADED = "TERMINO"
    const val ANALYZING = "ANALIZANDO"
    const val SYNCING = "SINCRONIZANDO"
    const val COMPARING = "COMPARANDO"
    const val FINISHED = "TERMINO"

    const val NULL_STR = "_ERROR"
    const val NULL_INT = 1230321
    const val NULL_DOUBLE = 1230321.123

    const val ONLINE = "ONLINE"
    const val OFFLINE = "OFFLINE"
    const val DEFAULT = ""
    const val USER_DEFAULT = "USER"

    const val BUY_TYPE = "COMPRA"
    const val PAY_TYPE = "PAGO"
    const val PAY_FORM_TITLE = "REGISTRAR \n"

    const val ID_BUY_TEMP:Long= 1
    const val ID_BUY_LIST:Long = 0

    const val DIVISION_KEY = "#DIV#"

    const val LANGUAGE_DEFAULT = "es"
    const val LANGUAGE_ENGLISH = "en"

    const val DEFAULT_ACCOUNT = 101
    const val DEFAULT_ACCOUNTING_ITEM = "GASTO"
    const val DEFAULT_COIN = "S/"

    const val MODE_DARK = true
    const val VIEW_ALL_ACCOUNTS = false
}