import numpy as np

n = 50

width = 800
height = 600
max_radius = 200

x = np.random.sample(n) * width
y = np.random.sample(n) * height
r = np.random.sample(n) * max_radius

res = np.column_stack((x, y, r))

np.savetxt('random_points_50_4.txt', res)
