package com.wintersoldier13.spendtacker.data

object KnownTags {

    fun getTags() : ArrayList<String> {
        val knownTags = ArrayList<String> ();
        knownTags.add("housing")
        knownTags.add("food")
        knownTags.add("family")
        knownTags.add("vehicle")
        knownTags.add("online_shopping")
        knownTags.add("clothing")
        knownTags.add("investment")
        knownTags.add("travel")
        knownTags.add("personal_care")
        knownTags.add("entertainment")
        knownTags.add("education")
        knownTags.add("groceries")
        knownTags.add("sports")
        knownTags.add("gadget")
        knownTags.add("tax")
        knownTags.add("commute")
        knownTags.add("insurance")
        knownTags.add("software")
        knownTags.add("misc")
        return knownTags;
    }
}