package gherkin.pickles;

import gherkin.SymbolCounter;
import gherkin.ast.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.*;

public class Compiler {

    public List<Pickle> compile(GherkinDocument gherkinDocument, String path) {
        List<Pickle> pickles = new ArrayList<>();
        Feature feature = gherkinDocument.getFeature();
        if (feature == null) {
            return pickles;
        }

        List<Tag> featureTags = feature.getTags();
        List<PickleStep> backgroundSteps = new ArrayList<>();

        for (ScenarioDefinition scenarioDefinition : feature.getChildren()) {
            if (scenarioDefinition instanceof Background) {
                backgroundSteps = pickleSteps(scenarioDefinition, path);
            } else if (scenarioDefinition instanceof Scenario) {
                compileScenario(pickles, backgroundSteps, (Scenario) scenarioDefinition, featureTags, path);
            } else {
                compileScenarioOutline(pickles, backgroundSteps, (ScenarioOutline) scenarioDefinition, featureTags, path);
            }
        }
        return pickles;
    }

    private void compileScenario(List<Pickle> pickles, List<PickleStep> backgroundSteps, Scenario scenario, List<Tag> featureTags, String path) {
        if (scenario.getSteps().isEmpty())
            return;

        List<PickleStep> steps = new ArrayList<>();
        steps.addAll(backgroundSteps);

        List<Tag> scenarioTags = new ArrayList<>();
        scenarioTags.addAll(featureTags);
        scenarioTags.addAll(scenario.getTags());

        steps.addAll(pickleSteps(scenario, path));

        Pickle pickle = new Pickle(
                scenario.getName(),
                steps,
                pickleTags(scenarioTags, path),
                singletonList(pickleLocation(scenario.getLocation(), path))
        );
        pickles.add(pickle);
    }

    private void compileScenarioOutline(List<Pickle> pickles, List<PickleStep> backgroundSteps, ScenarioOutline scenarioOutline, List<Tag> featureTags, String path) {
        if (scenarioOutline.getSteps().isEmpty())
            return;

        int exampleCount = 1;
        for (final Examples examples : scenarioOutline.getExamples()) {
            if (examples.getTableHeader() == null) continue;
            List<TableCell> variableCells = examples.getTableHeader().getCells();
            for (final TableRow values : examples.getTableBody()) {
                List<TableCell> valueCells = values.getCells();

                List<PickleStep> steps = new ArrayList<>();
                steps.addAll(backgroundSteps);

                List<Tag> tags = new ArrayList<>();
                tags.addAll(featureTags);
                tags.addAll(scenarioOutline.getTags());
                tags.addAll(examples.getTags());

                for (Step scenarioOutlineStep : scenarioOutline.getSteps()) {
                    String stepText = interpolate(scenarioOutlineStep.getText(), variableCells, valueCells);

                    // TODO: Use an Array of location in DataTable/DocString as well.
                    // If the Gherkin AST classes supported
                    // a list of locations, we could just reuse the same classes

                    PickleStep pickleStep = new PickleStep(
                            stepText,
                            createPickleArguments(scenarioOutlineStep.getArgument(), variableCells, valueCells, path),
                            asList(
                                    pickleLocation(values.getLocation(), path),
                                    pickleStepLocation(scenarioOutlineStep, path)
                            ),
                            scenarioOutlineStep.getKeyword().trim()
                    );
                    steps.add(pickleStep);
                }

                Pickle pickle = new Pickle(
                        interpolate(scenarioOutline.getName(), variableCells, valueCells) + " Example No." + exampleCount++,
                        steps,
                        pickleTags(tags, path),
                        asList(
                                pickleLocation(values.getLocation(), path),
                                pickleLocation(scenarioOutline.getLocation(), path)
                        )
                );

                pickles.add(pickle);
            }
        }
    }

    private List<Argument> createPickleArguments(Node argument, String path) {
        List<TableCell> noCells = emptyList();
        return createPickleArguments(argument, noCells, noCells, path);
    }

    private List<Argument> createPickleArguments(Node argument, List<TableCell> variableCells, List<TableCell> valueCells, String path) {
        List<Argument> result = new ArrayList<>();
        if (argument == null) return result;
        if (argument instanceof DataTable) {
            DataTable t = (DataTable) argument;
            List<TableRow> rows = t.getRows();
            List<PickleRow> newRows = new ArrayList<>(rows.size());
            for (TableRow row : rows) {
                List<TableCell> cells = row.getCells();
                List<PickleCell> newCells = new ArrayList<>();
                for (TableCell cell : cells) {
                    newCells.add(
                            new PickleCell(
                                    pickleLocation(cell.getLocation(), path),
                                    interpolate(cell.getValue(), variableCells, valueCells)
                            )
                    );
                }
                newRows.add(new PickleRow(newCells));
            }
            result.add(new PickleTable(newRows));
        } else if (argument instanceof DocString) {
            DocString ds = (DocString) argument;
            result.add(
                    new PickleString(
                            pickleLocation(ds.getLocation(), path),
                            interpolate(ds.getContent(), variableCells, valueCells)
                    )
            );
        } else {
            throw new RuntimeException("Unexpected argument type: " + argument);
        }
        return result;
    }

    private List<PickleStep> pickleSteps(ScenarioDefinition scenarioDefinition, String path) {
        List<PickleStep> result = new ArrayList<>();
        for (Step step : scenarioDefinition.getSteps()) {
            result.add(pickleStep(step, path));
        }
        return unmodifiableList(result);
    }

    private PickleStep pickleStep(Step step, String path) {
        return new PickleStep(
                step.getText(),
                createPickleArguments(step.getArgument(), path),
                singletonList(pickleStepLocation(step, path)),
                step.getKeyword().trim()
        );
    }

    private String interpolate(String name, List<TableCell> variableCells, List<TableCell> valueCells) {
        int col = 0;
        for (TableCell variableCell : variableCells) {
            TableCell valueCell = valueCells.get(col++);
            String header = variableCell.getValue();
            String value = valueCell.getValue();
            name = name.replace("<" + header + ">", value);
        }
        return name;
    }

    private PickleLocation pickleStepLocation(Step step, String path) {
        return new PickleLocation(
                path,
                step.getLocation().getLine(),
                step.getLocation().getColumn() + (step.getKeyword() != null ? SymbolCounter.countSymbols(step.getKeyword()) : 0)
        );
    }

    private PickleLocation pickleLocation(Location location, String path) {
        return new PickleLocation(path, location.getLine(), location.getColumn());
    }

    private List<PickleTag> pickleTags(List<Tag> tags, String path) {
        List<PickleTag> result = new ArrayList<>();
        for (Tag tag : tags) {
            result.add(pickleTag(tag, path));
        }
        return result;
    }

    private PickleTag pickleTag(Tag tag, String path) {
        return new PickleTag(pickleLocation(tag.getLocation(), path), tag.getName());
    }
}
