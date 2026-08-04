package com.certacure.lis.interfaces.middleware.flow_component.scheduler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import com.certacure.lis.interfaces.middleware.util.LowLevelUtils;

public class SchedulerProtocol {

	public static class BytesMessage implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public String toString() {
			return null;
			
		}

		

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			else
				return false;
			
		}

		@Override
		public int hashCode() {
			return -1;
		}
	}
}
