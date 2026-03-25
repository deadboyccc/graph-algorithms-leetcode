#include <climits>
#include <cmath>
#include <queue>
#include <set>
#include <vector>

using namespace std;

class Solution {
 public:
  int minCostConnectPoints(vector<vector<int>>& points) {
    int n = points.size();
    int total_cost = 0;
    int edges_used = 0;
    set<int> visited;

    // Min-heap storing {weight, point_index}
    priority_queue<pair<int, int>, vector<pair<int, int>>,
                   greater<pair<int, int>>>
        pq;

    pq.push({0, 0});

    while (edges_used < n && !pq.empty()) {
      auto [weight, curr] = pq.top();
      pq.pop();

      if (visited.count(curr)) continue;

      visited.insert(curr);
      total_cost += weight;
      edges_used++;

      for (int next = 0; next < n; ++next) {
        if (!visited.count(next)) {
          int dist = abs(points[curr][0] - points[next][0]) +
                     abs(points[curr][1] - points[next][1]);
          pq.push({dist, next});
        }
      }
    }

    return total_cost;
  }
};
