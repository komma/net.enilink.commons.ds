package net.enilink.commons.ds.graph;

class Edge<N, E> implements IEdge<N, E> {
	N start, end;
	E data;

	public Edge(N start, E data, N end) {
		this.start = start;
		this.data = data;
		this.end = end;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.iwu.traceability.core.graph.IEdge#getStart()
	 */
	public N getStart() {
		return start;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.iwu.traceability.core.graph.IEdge#getEnd()
	 */
	public N getEnd() {
		return end;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.iwu.traceability.core.graph.IEdge#getData()
	 */
	public E getData() {
		return data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Edge<?, ?> other = (Edge<?, ?>) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder("(").append(start).append(" -> ").append(end)
				.append(", ").append(data).append(")").toString();
	}
}
