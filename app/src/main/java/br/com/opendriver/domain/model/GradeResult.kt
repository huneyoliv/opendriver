package br.com.opendriver.domain.model

data class GradeResult(
    val gradeKm: TripGrade,
    val gradeHour: TripGrade,
    val gradeRating: TripGrade,
    val finalGrade: TripGrade,
    val lucroPerKm: Float,
    val lucroPerHour: Float
)
