package com.example.ksaturno.technicians

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R

/**
 * El Adaptador es el componente responsable de conectar los datos de los técnicos
 * con la lista visual (RecyclerView) que se muestra en pantalla. Actúa como un puente:
 * recibe una lista de objetos 'Technician' y sabe cómo crear y rellenar cada fila
 * de la lista con la información de cada técnico.
 *
 * @param technicians La lista inicial de técnicos a mostrar.
 * @param onEditClick Una función "callback". El Fragmento le pasa esta función al adaptador
 *                    para que el adaptador pueda "avisarle" al Fragmento cuando el usuario
 *                    pulsa el ícono de editar en una fila específica.
 * @param onDeleteClick Una función callback similar para el evento de borrado.
 */
class TechniciansAdapter(
    private var technicians: List<Technician>,
    private val onEditClick: (Technician) -> Unit,
    private val onDeleteClick: (Technician) -> Unit
) : RecyclerView.Adapter<TechniciansAdapter.TechnicianViewHolder>() {

    /**
     * Un método público que permite al Fragment (a través del ViewModel) actualizar
     * la lista de técnicos que muestra el adaptador.
     * @param newTechnicians La nueva lista de técnicos recibida desde la API.
     */
    fun updateTechnicians(newTechnicians: List<Technician>) {
        technicians = newTechnicians
        // Notifica al RecyclerView que los datos han cambiado y que necesita
        // redibujar la lista completa. Es la forma más simple de actualizar.
        notifyDataSetChanged()
    }

    /**
     * Este método es llamado por el RecyclerView cuando necesita crear una NUEVA fila (ViewHolder).
     * Esto solo sucede unas pocas veces: para las filas que caben en la pantalla y algunas más
     * para hacer el scroll suave. No se llama para cada elemento de la lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TechnicianViewHolder {
        // "Infla" (crea un objeto View) el layout XML 'item_technician.xml' que define
        // la apariencia de una sola fila.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_technician, parent, false)
        // Crea y devuelve una instancia del ViewHolder, que contendrá las referencias a las vistas de la fila.
        return TechnicianViewHolder(view)
    }

    /**
     * Este método es llamado por el RecyclerView para RELLENAR los datos de una fila en una
     * posición específica. Se reutilizan las filas que salen de la pantalla para mostrar nuevos datos.
     */
    override fun onBindViewHolder(holder: TechnicianViewHolder, position: Int) {
        // Obtiene el objeto 'Technician' correspondiente a esta posición en la lista.
        val technician = technicians[position]
        // Llama al método 'bind' del ViewHolder para que este se encargue de rellenar sus vistas.
        holder.bind(technician)
    }

    /**
     * Un método simple que devuelve el número total de elementos en la lista. El RecyclerView
     * lo usa para saber cuántos elementos debe manejar en total.
     */
    override fun getItemCount(): Int = technicians.size

    /**
     * Esta clase interna representa UNA SOLA FILA en la lista. Su propósito es mantener
     * las referencias a las vistas dentro de esa fila (los TextViews, ImageViews, etc.)
     * para evitar tener que buscarlas repetidamente con 'findViewById', lo cual es ineficiente.
     */
    inner class TechnicianViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.text_view_technician_name)
        private val emailTextView: TextView = itemView.findViewById(R.id.text_view_technician_email)
        private val editImageView: ImageView = itemView.findViewById(R.id.image_view_edit_technician)
        private val deleteImageView: ImageView = itemView.findViewById(R.id.image_view_delete_technician)

        /**
         * Este método se encarga de "atar" o "vincular" (bind) los datos de un objeto
         * 'Technician' específico a las vistas de esta fila.
         * @param technician El objeto con los datos a mostrar.
         */
        fun bind(technician: Technician) {
            // Asigna el nombre y el correo del técnico a los TextViews correspondientes.
            nameTextView.text = technician.nombre
            emailTextView.text = technician.correo

            // Configura los listeners para los íconos de acción.
            // Cuando se pulsa el ícono de editar, se invoca la función 'onEditClick'
            // que el Fragment nos pasó, devolviéndole el técnico de esta fila.
            editImageView.setOnClickListener { onEditClick(technician) }

            // Lo mismo para el ícono de eliminar.
            deleteImageView.setOnClickListener { onDeleteClick(technician) }
        }
    }
}
