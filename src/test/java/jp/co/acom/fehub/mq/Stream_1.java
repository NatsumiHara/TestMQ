package jp.co.acom.fehub.mq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class Stream_1  {

	private static final List<Integer> HHH = Arrays.asList(23, 2, 3, 4, 5, 5, 2, 23);
	private static final List<Integer> HHH3 = Arrays.asList(23, 7, 3, 5, 5);

	@Test
	void test() {
		for (Iterator<?> ii = HHH.iterator(); ii.hasNext();) {
			Integer aaa = (Integer) ii.next();
			if (aaa > 3)
				System.out.println(aaa);

		}

		System.out.println("----------");

//		HHH.stream().forEach(uu -> System.out.println(uu));
		HHH.stream().filter(aaa -> aaa > 3).forEach(System.out::println);

	}

	@Test
	void test2() {
		String aaa = null;
		List<String> list = new ArrayList<>();
		for (Iterator<?> ii = HHH.iterator(); ii.hasNext();) {
			aaa = ii.next().toString();

			list.add(aaa);
		}
//		List<String> list = new ArrayList<String>();
//		list.add(aaa);
		System.out.println(list);
		System.out.println("----------");

		List<String> list2 = new ArrayList<>();
		HHH.stream().map(a -> a.toString()).forEach(a -> list2.add(a));
		System.out.println(list2);

		System.out.println(HHH.stream().distinct().sorted().collect(Collectors.toList()));

	}

	@Test
	void test3() {
		boolean b = false;
		Integer aaa = null;
		for (Iterator<?> ii = HHH3.iterator(); ii.hasNext();) {
			aaa = (Integer) ii.next();
			System.out.println(aaa);

			if (aaa % 2 == 0) {
				b = true;
				break;
			}

		}
		if (b) {
			System.out.println("OK!");
		}
		System.out.println("----------");
		if (HHH3.stream().anyMatch(e -> e % 2 == 0)) {
			System.out.println("OK!");
		}

	}

	@Test
	void test4() {
		Integer a = null;
		boolean b = false;
		for (Iterator<?> ii = HHH.iterator(); ii.hasNext();) {
			a = (Integer) ii.next();
			System.out.println(a);

			if (a % 2 == 0) {
				b = true;
				break;
			}
		}

		if (b) {
			System.out.println("NG");
		} else {
			System.out.println("OK");
		}

		System.out.println("----------");
		if (HHH.stream().allMatch(e -> e % 2 != 0)) {
			System.out.println("OK!");
		}

	}
}