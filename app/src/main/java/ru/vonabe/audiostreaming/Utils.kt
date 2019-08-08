package ru.vonabe.audiostreaming

class Utils {

    companion object{
        fun isValidEmailAddress(email: String): Boolean {
            if(email.isNullOrEmpty())return false
            val ePattern =
                "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
            val p = java.util.regex.Pattern.compile(ePattern)
            val m = p.matcher(email)
            return m.matches()
        }
    }

}