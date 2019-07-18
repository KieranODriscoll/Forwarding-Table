import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

public class LinkState {

	public static void main(String[] args) {

		int[][] costGraph;
		int[] gatewayRouters;
		int[] sourceNodes;
		int n;

		String l;
		Scanner input = new Scanner(System.in);
		n = Integer.parseInt(input.nextLine());

		costGraph = new int[n][];

		for (int i = 0; i < n; i++) {

			l = input.nextLine();
			int row[] = Arrays.stream(l.split(" ")).mapToInt(Integer::parseInt).toArray();
			costGraph[i] = row;
		}

		l = input.nextLine();
		gatewayRouters = Arrays.stream(l.split(" ")).mapToInt(Integer::parseInt).toArray();
		sourceNodes = new int[n - gatewayRouters.length];

		int i = 1;
		int k = 1;
		while (i <= sourceNodes.length) {
			final int j = k;
			boolean found = IntStream.of(gatewayRouters).anyMatch(e -> e == j);
			if (!found) {
				sourceNodes[i - 1] = j;
				i++;
			}
			k++;
		}

		LS(costGraph, sourceNodes, gatewayRouters, n);

		input.close();

	}

	public static void LS(int[][] costGraph, int[] sourceNodes, int gatewayRouters[], int n) {

		for (int i = 0; i < sourceNodes.length; i++) {

			int[] visited = new int[n];
			int[] sourceCost = new int[n];
			int[] previousNode = new int[n];

			int source;

			visited[0] = sourceNodes[i];
			source = sourceNodes[i];

			/* Initialization Starts */
			for (int j = 0; j < n; j++) {
				if (source == j + 1) {
					sourceCost[j] = 0;
					previousNode[j] = 0;
				} else {
					if (costGraph[source - 1][j] < 0) {
						sourceCost[j] = -1;
						previousNode[j] = -1;
					} else {
						sourceCost[j] = costGraph[source - 1][j];
						previousNode[j] = source;
					}
				}
			}
			/* Initialization Ends */

			/* Loop Starts */

			boolean notAllVisited = true;
			int k = 1;
			while (notAllVisited) {
				int minimum = 2147483647;
				int node = -1;
				for (int l = 0; l < n; l++) {
					if (sourceCost[l] > 0) {
						int m = l + 1;
						boolean beenVisited = IntStream.of(visited).anyMatch(e -> e == m);
						if (!beenVisited) {
							if (sourceCost[l] < minimum) {
								minimum = sourceCost[l];
								node = l + 1;
							}
						}
					}

				}

				if (node == -1) {
					break;
				}

				visited[k] = node;

				for (int l = 0; l < n; l++) {
					if (sourceCost[l] == -1 && costGraph[visited[k] - 1][l] == -1) {
						continue;
					} else if (sourceCost[l] != -1 && costGraph[visited[k] - 1][l] == -1) {
						continue;
					} else if (sourceCost[l] == -1 && costGraph[visited[k] - 1][l] != -1) {
						sourceCost[l] = costGraph[visited[k] - 1][l] + sourceCost[visited[k] - 1];
						previousNode[l] = visited[k];
					} else if (sourceCost[l] != 0 && costGraph[visited[k] - 1][l] != 0) {
						if (sourceCost[l] > sourceCost[visited[k] - 1] + costGraph[visited[k] - 1][l]) {
							sourceCost[l] = sourceCost[visited[k] - 1] + costGraph[visited[k] - 1][l];
							previousNode[l] = visited[k];
						}
					}
				}

				k++;

				notAllVisited = IntStream.of(visited).anyMatch(e -> e == 0);
			}

			/* Loop Ends */

			printForwardingTable(sourceNodes, sourceCost, previousNode, gatewayRouters, source);

		}

	}

	public static void printForwardingTable(int[] sourceNodes, int[] sourceCost, int previousNode[],
			int gatewayRouters[], int source) {

		System.out.println("Forwarding Table for " + source);
		System.out.println("To \tCost \tNext Hop");
		for (int j = 0; j < gatewayRouters.length; j++) {

			int costToGateway = -1;
			int nextHop = -1;
			int previous = -1;

			previous = gatewayRouters[j];
			while (previous != source) {
				nextHop = previous;
				if (previousNode[nextHop - 1] == -1) {
					nextHop = -1;
					break;
				} else {
					previous = previousNode[nextHop - 1];
				}
			}
			costToGateway = sourceCost[gatewayRouters[j] - 1];
			System.out.println(gatewayRouters[j] + " \t" + costToGateway + " \t" + nextHop);
		}

		System.out.println("");

	}

}
