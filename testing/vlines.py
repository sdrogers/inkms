"""
Small demonstration of the hlines and vlines plots.
"""
# http://matplotlib.org/api/pyplot_api.html
import matplotlib.pyplot as plt
import numpy as np
import numpy.random as rnd


def f(t):
    return t


t = np.arange(0.0, 5.0, 0.1)
s = f(t)

fig = plt.figure(figsize=(12, 6))
# vax = fig.add_subplot(121)
# hax = fig.add_subplot(122)

plt.plot(t, s, 'b^')
plt.vlines(t, [0], s)
#vax.set_xlabel('time (s)')
#vax.set_title('Vertical lines demo')

# hax.plot(s, t, 'b^')
# hax.hlines(t, [0], s, lw=2)
# hax.set_xlabel('time (s)')
# hax.set_title('Horizontal lines demo')

plt.show()
