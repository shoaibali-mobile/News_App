package com.shoaib.demodatadog.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shoaib.demodatadog.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val categories: List<String>,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var _selectedPosition = 0
    val selectedPosition: Int get() = _selectedPosition

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], position == _selectedPosition)
    }

    override fun getItemCount() = categories.size

    private fun selectCategory(position: Int) {
        val oldPosition = _selectedPosition
        _selectedPosition = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(_selectedPosition)
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String, isSelected: Boolean) {
            binding.root.text = category.replaceFirstChar { it.uppercase() }
            binding.root.isChecked = isSelected
            binding.root.setOnClickListener {
                selectCategory(adapterPosition)
                onCategoryClick(category)
            }
        }
    }
}

