package com.github.ezauton.core.trajectory.geometry

class ImmutableMatrix(private val rows: List<ImmutableVector>) : Iterable<ImmutableMatrix.MatrixEntry> {

    override fun iterator(): Iterator<MatrixEntry> {
        return kotlin.sequences.iterator {
            rows.forEachIndexed {i, row ->
                row.elements.forEachIndexed { j, value ->
                    yield(MatrixEntry(i,j, value))
                }
            }
        }
    }

    init {
        require(rows.groupingBy { it.dimension }.eachCount().size == 1) { "Rows (${rows}) must be of the same length!" }
    }

    val width = rows[0].dimension
    val height = rows.size

    operator fun get(row: Int): ImmutableVector {
        return rows[row]
    }

    fun getColumn(column: Int): ImmutableVector {
        val elements = rows.map { it[column] }
        return ImmutableVector(elements)
    }

    operator fun get(row: Int, column: Int): Double {
        val vector = this[row]
        return vector[column]
    }

    operator fun times(otherMatrix: ImmutableMatrix): ImmutableMatrix {

        val matrixEntries = ArrayList<MatrixEntry>()

        for (j in 0 until otherMatrix.width) {
            for (i in 0 until height) {
                val element = get(i).dot(otherMatrix.getColumn(j))
                matrixEntries.add(MatrixEntry(i,j,element))
            }
        }
        return matrixEntries.matrix()
    }

    data class MatrixEntry(val row: Int, val column: Int, val value: Double)
}

private fun List<ImmutableMatrix.MatrixEntry>.matrix(): ImmutableMatrix {
    val sortedGrouping = this
            .groupBy{ it.row }
            .toSortedMap()

    val vectorList = sortedGrouping
            .map { it.value.vector() }

    return ImmutableMatrix(vectorList)

}

fun List<ImmutableMatrix.MatrixEntry>.vector(): ImmutableVector {
    val entries = this.sortedBy { it.column }.map { it.value }
    return ImmutableVector(entries)
}
