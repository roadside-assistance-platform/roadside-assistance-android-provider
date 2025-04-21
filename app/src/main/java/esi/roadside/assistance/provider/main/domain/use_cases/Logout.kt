package esi.roadside.assistance.provider.main.domain.use_cases

import esi.roadside.assistance.provider.main.domain.repository.MainRepo

class Logout(private val mainRepo: MainRepo) {
    suspend operator fun invoke() = mainRepo.logout()
}