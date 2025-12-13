package pt.iade.lane.components

import com.google.android.gms.maps.model.BitmapDescriptorFactory

object EventCategoryColors {

    fun hueForCategory(categoryId: Int?): Float {
        return when (categoryId) {
            1 -> BitmapDescriptorFactory.HUE_ORANGE   // Desporto
            2 -> BitmapDescriptorFactory.HUE_VIOLET   // Música & Concertos
            3 -> BitmapDescriptorFactory.HUE_AZURE    // Estudos & Workshops
            4 -> BitmapDescriptorFactory.HUE_CYAN     // Networking & Carreira
            5 -> BitmapDescriptorFactory.HUE_ROSE     // Social & Noite
            6 -> BitmapDescriptorFactory.HUE_GREEN    // Gaming & eSports
            7 -> BitmapDescriptorFactory.HUE_MAGENTA  // Arte & Cultura
            8 -> BitmapDescriptorFactory.HUE_YELLOW   // Voluntariado & Comunidade
            9 -> BitmapDescriptorFactory.HUE_BLUE     // Tecnologia & Startups
            10 -> BitmapDescriptorFactory.HUE_RED   // Saúde & Bem-estar
            else -> BitmapDescriptorFactory.HUE_RED   // Categoria default
        }
    }
}