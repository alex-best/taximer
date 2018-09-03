package ru.taximer.taxiandroid.network.usecases

import ru.taximer.taxiandroid.network.TaxiApi


///////////////////////////////////////////////////////////////////////////
// Base Usecases
///////////////////////////////////////////////////////////////////////////

class BaseUsecases(
        internal val endpoints: TaxiApi
) : UserUsecases by UserUsecasesImpl(endpoints),
    TaxiUsecases by TaxiUsecasesImpl(endpoints)