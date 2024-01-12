package com.wintersoldier13.spendtacker.data

object TargetSms {
    fun getKnownSmsBody() : ArrayList<Pair<String, String>> {
        val knownSms = ArrayList<Pair<String, String>> ()
        knownSms.add(Pair("FED_UPI", "debited from your"))
        knownSms.add(Pair("SBI_UPI", "debited by"))
        knownSms.add(Pair("ICICI_CreditC", "spent on ICICI"))
        knownSms.add(Pair("PayTM_UPI","sent to"))

       return knownSms
    }

    fun isThisTransactional(smsBody : String) : String{
        for (knownStrings in this.getKnownSmsBody()) {
            if (smsBody.contains(knownStrings.second, ignoreCase = true)){
                return knownStrings.first;
            }
        }
        return ""
    }
}
