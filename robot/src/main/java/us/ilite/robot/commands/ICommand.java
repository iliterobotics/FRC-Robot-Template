package us.ilite.commands;

public interface ICommand {
    void init(double pNow);
    void update(double pNow);
    void shutdown(double pNow);
}