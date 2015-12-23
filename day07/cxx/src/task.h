#pragma once

#include <future>

template <class T>
struct task {
    std::promise<std::shared_future<T> > p;
    std::shared_future<std::shared_future<T> > f;
    task()
        : p{}
        , f(p.get_future())
    {
    }
    template <class W, class... Args>
    void set(W&& work, Args&&... args)
    {
        p.set_value(std::async(std::launch::deferred, std::forward<W>(work), std::forward<Args>(args)...));
    }
    T get()
    {
        return f.get().get();
    }
};
