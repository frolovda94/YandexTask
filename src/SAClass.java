import java.util.*;

public class SAClass {

	private class State {
		int length;
		int link;
		Map<Character, Integer> next = new HashMap<Character, Integer>();
	}

	private State[] st;

	public SAClass(String s) {
		int n = s.length();
		st = new State[Math.max(2, 2 * n - 1)];
		st[0] = new State();
		st[0].link = -1;
		int last = 0;
		int size = 1;
		for (char c : s.toCharArray()) {
			int cur = size++;
			st[cur] = new State();
			st[cur].length = st[last].length + 1;
			int p;
			for (p = last; p != -1 && !st[p].next.containsKey(c); p = st[p].link) {
				st[p].next.put(c, cur);
			}
			if (p == -1) {
				st[cur].link = 0;
			} else {
				int q = st[p].next.get(c);
				if (st[p].length + 1 == st[q].length)
					st[cur].link = q;
				else {
					int clone = size++;
					st[clone] = new State();
					st[clone].length = st[p].length + 1;
					st[clone].next.putAll(st[q].next);
					st[clone].link = st[q].link;
					for (; p != -1 && st[p].next.get(c) == q; p = st[p].link)
						st[p].next.put(c, clone);
					st[q].link = clone;
					st[cur].link = clone;
				}
			}
			last = cur;
		}
	}

	public String lcs(String b) {
		int len = 0;
		int bestLen = 0;
		int bestPos = -1;
		for (int i = 0, cur = 0; i < b.length(); ++i) {
			char c = b.charAt(i);
			if (!st[cur].next.containsKey(c)) {
				for (; cur != -1 && !st[cur].next.containsKey(c); cur = st[cur].link) {
				}
				if (cur == -1) {
					cur = 0;
					len = 0;
					continue;
				}
				len = st[cur].length;
			}
			++len;
			cur = st[cur].next.get(c);
			if (bestLen < len) {
				bestLen = len;
				bestPos = i;
			}
		}
		return b.substring(bestPos - bestLen + 1, bestPos + 1);
	}
}