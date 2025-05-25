package esi.roadside.assistance.provider.main.domain.use_cases

import esi.roadside.assistance.provider.core.util.account.AccountManager
import esi.roadside.assistance.provider.main.domain.repository.MainRepo

class FetchServices(private val repo: MainRepo, private val accountManager: AccountManager) {
    suspend operator fun invoke() = repo.fetchServices(accountManager.getUser()?.id ?:
        throw IllegalStateException("User not logged in"))
}