package esi.roadside.assistance.provider.main.domain.use_cases

import esi.roadside.assistance.provider.auth.domain.repository.AuthRepo
import esi.roadside.assistance.provider.core.domain.util.onSuccess
import esi.roadside.assistance.provider.core.util.account.AccountManager

class Refresh(private val repo: AuthRepo, private val manager: AccountManager) {
    suspend operator fun invoke() {
        manager.getUser()?.let { user ->
            repo.getIsApproved(user.id).onSuccess {
                manager.updateIsApproved(it)
            }
        }
    }
}