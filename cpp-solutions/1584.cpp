```cpp
#include <climits>
#include <cmath>
#include <queue>
#include <unordered_set>
#include <vector>

using namespace std;

/**
 * Problem: Min Cost to Connect All Points (Minimum Spanning Tree)
 *
 * Goal: Connect all 'n' points such that the total distance is minimized.
 *
 * Key Points:
 * 1. Points are (x, y) coordinates on a 2D grid.
 * 2. Distance is Manhattan Distance: |x1 - x2| + |y1 - y2|.
 * 3. All points must be connected (directly or indirectly).
 * 4. Return the minimum sum of edge weights.
 */
class Solution {
 private:
  struct Point {
    int x, y;
  };

  struct State {
    int cost, index;
    bool operator>(const State& other) const { return cost > other.cost; }
  };

  static int manhattanDist(const Point& a, const Point& b) {
    return abs(a.x - b.x) + abs(a.y - b.y);
  }

 public:
  int minCostConnectPoints(vector<vector<int>>& rawPoints) {

    // 1. Build typed point list for expressive access (p.x / p.y vs points[i][0])
    vector<Point> nodes;
    nodes.reserve(rawPoints.size());
    for (const auto& p : rawPoints)
      nodes.push_back({p[0], p[1]});

    const int n = nodes.size();
    int totalCost = 0;
    unordered_set<int> settled;

    // 2. Min-heap of (cost, index) — always expand the cheapest reachable node next
    priority_queue<State, vector<State>, greater<State>> pq;
    pq.push({0, 0});

    while (settled.size() < static_cast<size_t>(n) && !pq.empty()) {
      auto [cost, node] = pq.top();
      pq.pop();

      if (!settled.insert(node).second) continue; // already settled

      totalCost += cost;

      // 3. Enqueue unsettled neighbors with their manhattan distance to current node
      for (int neighbor = 0; neighbor < n; ++neighbor) {
        if (!settled.count(neighbor))
          pq.push({manhattanDist(nodes[node], nodes[neighbor]), neighbor});
      }
    }

    return totalCost;
  }
};
```