# 🧩 Set Mismatch (LeetCode) — Complete Guide

## 📌 Problem Summary

You are given an array `nums` of size `n` containing numbers from:

1 → n

However:
- One number is **duplicated**
- One number is **missing**

### ✅ Goal:
Return:
[duplicate, missing]

---

## 🔍 Example

Input:  [3, 2, 2]  
Output: [2, 1]

- `2` appears twice → duplicate
- `1` is missing

---

## ❌ Why the Original Approach Failed

### Your logic:
```kotlin
if (element != i + 1)
```

### ❗ Problem:
This assumes:
index i → value should be i + 1

👉 This only works if the array is **sorted**

---

# ✅ Solution 1: Sign Marking (Visited Trick)

## 💡 Core Idea

Use the array itself as a **visited map**:

- Each value `x` maps to index `x - 1`
- Mark visited indices as **negative**
- If already negative → duplicate found

---

## 🧠 Algorithm

### Step 1: Detect duplicate
```kotlin
val index = abs(nums[i]) - 1

if (nums[index] < 0)
    duplicate found
else
    nums[index] = -nums[index]
```

### Step 2: Detect missing
```kotlin
if (nums[i] > 0)
    missing = i + 1
```

---

## 🔍 Example Walkthrough

nums = [3, 2, 2]

Step 1:
[3, 2, -2]
[3, -2, -2]
duplicate = 2

Step 2:
[3, -2, -2]
missing = 1

---

## 💻 Kotlin Implementation

```kotlin
class Solution {
    fun findErrorNums(nums: IntArray): IntArray {
        var duplicate = -1
        var missing = -1

        for (i in nums.indices) {
            val index = kotlin.math.abs(nums[i]) - 1

            if (nums[index] < 0) {
                duplicate = kotlin.math.abs(nums[i])
            } else {
                nums[index] = -nums[index]
            }
        }

        for (i in nums.indices) {
            if (nums[i] > 0) {
                missing = i + 1
                break
            }
        }

        return intArrayOf(duplicate, missing)
    }
}
```

---

# ⚡ Solution 2: Cyclic Sort

## 💡 Core Idea

value x → index x - 1

---

## 🔁 Algorithm

```kotlin
while (i < nums.size) {
    val correct = nums[i] - 1

    if (nums[i] != nums[correct]) {
        val temp = nums[i]
        nums[i] = nums[correct]
        nums[correct] = temp
    } else {
        i++
    }
}
```

---

## 🔍 Example

nums = [3, 2, 2]

→ [2, 2, 3]

Mismatch:
duplicate = 2  
missing = 1

---

# ⚖️ Comparison

| Approach        | Style            | Key Insight              |
|----------------|------------------|--------------------------|
| Sign Marking   | Detection        | Negative = visited       |
| Cyclic Sort    | Placement        | Value → correct index    |

---

# 🧭 Key Takeaways

- ❌ Index comparison is unreliable
- ✅ Map value → index
- ✅ Modify array in-place
- ✅ Recognize 1..n patterns

---

# 🚀 Complexity

| Solution        | Time | Space |
|----------------|------|-------|
| Sign Marking   | O(n) | O(1)  |
| Cyclic Sort    | O(n) | O(1)  |

---

## 🏁 Final Thought

This problem is about:

Mapping values to indices efficiently.
